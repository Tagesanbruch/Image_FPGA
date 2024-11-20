BUILD_DIR = ./build

export PATH := $(PATH):$(abspath ./utils)

test:
	mill -i __.test

verilog:
	mkdir -p $(BUILD_DIR)
	mill -i __.test.runMain Elaborate -td $(BUILD_DIR)
	
debug:
	mkdir -p $(BUILD_DIR)
	mill -i __.test.runMain Elaborate -td $(BUILD_DIR) --full-stacktrace

help:
	mill -i __.test.runMain Elaborate --help

compile:
	mill -i __.compile

bsp:
	mill -i mill.bsp.BSP/install

reformat:
	mill -i __.reformat

checkformat:
	mill -i __.checkFormat

count:
	@echo "Counting lines in .scala files..."
	@echo "Total lines:"
	@find ./src -name '*.scala' | xargs wc -l
	@echo "Non-empty lines:"
	@find ./src -name '*.scala' | xargs grep -v '^$$' | wc -l
# @echo "Non-empty lines without comments:"
# @find ./src -name '*.scala' | xargs grep -v '^$$' | grep -v '^\s*//' | wc -l

clean:
	-rm -rf $(BUILD_DIR)
	-rm -rf verilator/isp/*.log verilator/isp/*.vcd

.PHONY: test verilog help compile bsp reformat checkformat clean

sim:
	$(call git_commit, "sim RTL") # DO NOT REMOVE THIS LINE!!!
	@echo "\033[0;31mStart Chisel simulation. Note: Last vsrc will be backuped, NOTICE that no important verilog source file!\033[0m"
	@if diff -r ./src ./.src; then \
		echo "No changes in ./src"; \
		cd ./verilator/isp && make run ; \
	else \
		echo "Changes detected in ./src"; \
		make verilog || exit 1; \
		cp -r ./verilator/isp/vsrc ./verilator/isp/vsrc_backup; \
		rm -rf ./verilator/isp/vsrc/*; \
		cp ./build/*.v ./verilator/isp/vsrc; \
		cp ./build/*.sv ./verilator/isp/vsrc; \
		rm -rf ./.src; \
		cp -r ./src ./.src; \
		cd ./verilator/isp && make run ; \
	fi

socsim:
	$(call git_commit, "sim RTL") # DO NOT REMOVE THIS LINE!!!
	@echo "\033[0;31mStart Chisel simulation. Note: Last vsrc will be backuped, NOTICE that no important verilog source file!\033[0m"
	@if diff -r ./src ./.src; then \
		echo "No changes in ./src"; \
		cd ./verilator/soc && make run ; \
	else \
		echo "Changes detected in ./src"; \
		make verilog; \
		cp -r ./verilator/soc/vsrc ./verilator/soc/vsrc_backup; \
		rm -rf ./verilator/soc/vsrc/*; \
		cp ./build/*.v ./verilator/soc/vsrc; \
		cp ./build/*.sv ./verilator/soc/vsrc; \
		rm -rf ./.src; \
		cp -r ./src ./.src; \
		cd ./verilator/soc && make run ; \
	fi

bin2hex:
	@if [ -z "$(FILE)" ]; then \
	    echo "Please specify a file. Usage: make bin2hex FILE=<FILE>"; \
	else \
	    hexdump -v -e '/4 "%08x\n"' $(FILE) > $(FILE).hex; \
	fi

gtkwave:
	cd ./verilator/isp && make gtkwave

preprocess:
	cd ./verilator/isp && make preprocess

verify:
	cd ./verilator/isp && make verify

verifyrgb:
	cd ./verilator/isp && make verifyrgb

# ---------------------AM TARGET--------------------------

ifeq ($(strip $(IMG)),)
run:
	@echo "Please specify a Image."
runsoc:
	@echo "Please specify a Image."
else
run:
	$(call git_commit, "NPC program run")
	@echo make bin2hex FILE=$(IMG)
	make bin2hex FILE=$(IMG)
	cp $(IMG).hex verilator/isp/prog
	# @echo "\033[0;31mStart Chisel simulation. Note: Last vsrc will be backuped, NOTICE that no important verilog source file!\033[0m"
	# make verilog
	# cp -r ./verilator/isp/vsrc ./verilator/isp/vsrc_backup
	# rm -rf ./verilator/isp/vsrc/*
	# cp ./build/*.v ./verilator/isp/vsrc
	cd ./verilator/isp && make run FILE=$(IMG) ELF=$(ELF)
runsoc:
	$(call git_commit, "NPC_SOC program run")
	@echo make bin2hex FILE=$(IMG)
	make bin2hex FILE=$(IMG)
	cp $(IMG).hex verilator/soc/prog
	# @echo "\033[0;31mStart Chisel simulation. Note: Last vsrc will be backuped, NOTICE that no important verilog source file!\033[0m"
	# make verilog
	# cp -r ./verilator/soc/vsrc ./verilator/soc/vsrc_backup
	# rm -rf ./verilator/soc/vsrc/*
	# cp ./build/*.v ./verilator/soc/vsrc
	cd ./verilator/soc && make run FILE=$(IMG) ELF=$(ELF)
endif

ifeq ($(strip $(IMG)),)
perf_test:
	@echo "Please specify a Image."
else
perf_test:
	$(call git_commit, "NPC program run")
	@echo make bin2hex FILE=$(IMG)
	make bin2hex FILE=$(IMG)
	cp $(IMG).hex verilator/isp/prog
	# @echo "\033[0;31mStart Chisel simulation. Note: Last vsrc will be backuped, NOTICE that no important verilog source file!\033[0m"
	# make verilog
	# cp -r ./verilator/isp/vsrc ./verilator/isp/vsrc_backup
	# rm -rf ./verilator/isp/vsrc/*
	# cp ./build/*.v ./verilator/isp/vsrc
	cd ./verilator/isp && make perf_test FILE=$(IMG) ELF=$(ELF)
endif

# -include ../Makefile
