ifneq ($(words $(CURDIR)),1)
 $(error Unsupported: GNU Make cannot build in directories containing spaces, build elsewhere: '$(CURDIR)')
endif

######################################################################

# If $VERILATOR_ROOT isn't in the environment, we assume it is part of a
# package install, and verilator is in your path. Otherwise find the
# binary relative to $VERILATOR_ROOT (such as when inside the git sources).
ifeq ($(VERILATOR_ROOT),)
VERILATOR = verilator
else
export VERILATOR_ROOT
VERILATOR = $(VERILATOR_ROOT)/bin/verilator
endif
VERILATOR_CFLAGS += -MMD --build -cc --trace-fst \
				-O3 --x-assign fast --x-initial fast --noassert -Wno-WIDTHEXPAND -Wno-WIDTHTRUNC
TOPNAME = TOP
WORK_DIR  = $(shell pwd)
INC_PATH := $(WORK_DIR)/inc $(INC_PATH)
TEST_BIN = $(FILE)
TEST ?= example

VSRC_PATH ?= $(abspath ./vsrc/$(TEST))

ifeq ($(wildcard $(VSRC_PATH)),)
	$(error Directory $(VSRC_PATH) does not exist)
endif


ifeq ($(strip $(ELF)),)
	ELFFLAGS ?=
else
    ELFFLAGS = --elf=$(ELF)
endif


BUILD_DIR = ./build/$(TEST)
OBJ_DIR = $(BUILD_DIR)/obj_dir
BIN = $(BUILD_DIR)/$(TOPNAME)
default: $(BIN)
	
$(shell mkdir -p $(BUILD_DIR))

VSRCS = $(shell find $(VSRC_PATH) -name "*.v" -or -name "*.sv")
CSRCS = $(shell find $(abspath ./csrc) -name "*.c" -or -name "*.cc" -or -name "*.cpp")
CXXSRCS = $(shell find $(abspath ./csrc) -name "*.cc" -or -name "*.cpp")

ifeq ($(strip $(LOG)),)
	LOGFLAGS ?=
else
    LOGFLAGS = --log=$(LOG)
endif

# llvm
CXXFLAGS += $(shell llvm-config-11 --cxxflags) -fPIE
LIBS += $(shell llvm-config-11 --libs)
# rules for verilator
LIBS += -lreadline -ldl -pie
INCFLAGS = $(addprefix -I , $(INC_PATH))
CXXFLAGS += $(INCFLAGS) -DTOP_NAME="\"V$(TOPNAME)\"" -fsanitize=address
# CXXFLAGS += -U__FILE__ -D__FILE__='"$(subst $(dir $<),,$<)"'
LDFLAGS += -fsanitize=address $(LIBS)

IMAGE_PATH = ./inputImage
CSV_PATH = $(abspath ./tempFile/originalImage_csv)
OUTPUT_PATH = $(abspath ./tempFile/outputImage_csv)
OUTPUTIMAGE_PATH = $(abspath ./outputImage)
TEST_TARGET ?= lena20k

CSVFLAGS = --csv=$(CSV_PATH)/$(TEST_TARGET).csv
PARAMFLAGS = --param=$(CSV_PATH)/$(TEST_TARGET).txt
OUTPUTFLAGS = --output=$(OUTPUT_PATH)
TARGETFLAGS = --target=$(TEST_TARGET)
$(BIN): $(VSRCS) $(CSRCS)
	rm -rf $(OBJ_DIR)
	$(VERILATOR) $(VERILATOR_CFLAGS) \
		--top-module $(TOPNAME) $^ \
		$(addprefix -CFLAGS , $(CXXFLAGS)) $(addprefix -LDFLAGS , $(LDFLAGS))\
		--Mdir $(OBJ_DIR) --exe -o $(abspath $(BIN))

all: default

preprocess:
	@echo "Preprocess all image to csv"
	@mkdir -p $(CSV_PATH)
	python3 utils/image_to_csv.py -i $(IMAGE_PATH) -o $(CSV_PATH)

run: $(BIN)
	@mkdir -p $(OUTPUT_PATH)
	$(BIN) $(CSVFLAGS) $(PARAMFLAGS) $(OUTPUTFLAGS) $(LOGFLAGS) $(ELFFLAGS) $(TEST_BIN) $(TARGETFLAGS)

verify: 
	@echo "Verify image"
	python3 utils/csv_to_image.py -i $(OUTPUT_PATH) -o $(OUTPUTIMAGE_PATH)

verifyrgb: 
	@echo "Verify image"
	python3 utils/csv_to_image_rgb.py -i $(OUTPUT_PATH) -o $(OUTPUTIMAGE_PATH)


gtkwave:
	gtkwave ./wave.fst ./gtkwave.gtkw

######################################################################

maintainer-copy::
clean mostlyclean distclean maintainer-clean::
	-rm -rf $(BUILD_DIR) *.log *.dmp *.fst *.vpd core
.PHONY: default all clean run perf_test
