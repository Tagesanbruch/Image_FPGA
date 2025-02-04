#include <getopt.h>
#include <stdio.h>
#include "VISP.h"
#include "VISP___024root.h"

#include <fstream>
#include <string>

std::ofstream csv_file;
std::ofstream param_file_stream;
int line_count = 0;
int data_count = 0;

#define ISP_MODE_RGB888 0
#define ISP_MODE_GRAY 2
#define ISP_MODE_YCBCR422 3
#define ISP_MODE_YCBCR420 6
#define ISP_MODE_YCBCR444 7
#define ISP_MODE_RAW_BGGR 4

// #ifdef CONFIG_TRACE
#define TRACE
// #endif
#ifdef TRACE
#ifndef CONFIG_TRACE_MAX
#define CONFIG_TRACE_MAX 20000000
#endif
#include "verilated_fst_c.h"

// #define USE_DIV

#ifdef USE_DIV
#define CLK_DIV 6
#endif

#define INPUT_RAW
// #define INPUT_RGB

VerilatedFstC* tfp = NULL;
#endif
VerilatedContext* contextp = NULL;
VISP* top = NULL;
int trace_time = 0;

int image_width = 640, image_height = 480;
int hcnt = 0;
char* image_file;
char* param_file;
char* output_dir;
char* output_name;
std::string csv_file_path;
std::string param_file_path;

bool prev_io_post_isp_bus_frame_vsync = false;
bool prev_io_post_isp_bus_frame_href = false;
bool prev_io_post_isp_bus_frame_clken = false;

void monitor_output() {
  int post_isp_mode = top->io_post_isp_bus_frame_mode;
  // posedge clk
  if (top->clock) {
    if (top->io_post_isp_bus_frame_href && top->io_post_isp_bus_frame_vsync) {
      if (post_isp_mode == ISP_MODE_YCBCR422 || post_isp_mode == ISP_MODE_YCBCR420 || post_isp_mode == ISP_MODE_YCBCR444) {
        // YCbCr mode
        csv_file << static_cast<int>(top->io_post_isp_bus_img_Y) << ","
                 << static_cast<int>(top->io_post_isp_bus_img_Cb) << ","
                 << static_cast<int>(top->io_post_isp_bus_img_Cr) << "\n";
        printf("Y: %d, Cb: %d, Cr: %d, x: %d, y: %d\n",
               static_cast<int>(top->io_post_isp_bus_img_Y),
               static_cast<int>(top->io_post_isp_bus_img_Cb),
               static_cast<int>(top->io_post_isp_bus_img_Cr), data_count, line_count);
      } else if (post_isp_mode == ISP_MODE_GRAY) {
        // YCbCr mode
        csv_file << static_cast<int>(top->io_post_isp_bus_img_Y) << ","
                 << static_cast<int>(top->io_post_isp_bus_img_Cb) << ","
                 << static_cast<int>(top->io_post_isp_bus_img_Cr) << "\n";
        printf("Y: %d, Cb: %d, Cr: %d, x: %d, y: %d\n",
               static_cast<int>(top->io_post_isp_bus_img_Y),
               static_cast<int>(top->io_post_isp_bus_img_Cb),
               static_cast<int>(top->io_post_isp_bus_img_Cr), data_count, line_count);
      } else if (post_isp_mode == ISP_MODE_RGB888) {
        // RGB888 mode
        csv_file << static_cast<int>(top->io_post_isp_bus_img_red) << ","
                 << static_cast<int>(top->io_post_isp_bus_img_green) << ","
                 << static_cast<int>(top->io_post_isp_bus_img_blue) << "\n";
        printf("R: %d, G: %d, B: %d, x: %d, y: %d\n",
               static_cast<int>(top->io_post_isp_bus_img_red),
               static_cast<int>(top->io_post_isp_bus_img_green),
               static_cast<int>(top->io_post_isp_bus_img_blue), data_count, line_count);
      } else if (post_isp_mode == ISP_MODE_RAW_BGGR) {
        // RAW BGGR mode
        csv_file << static_cast<int>(top->io_post_isp_bus_img_raw) << "\n";
        printf("RAW: %d, x: %d, y: %d\n",
               static_cast<int>(top->io_post_isp_bus_img_raw), data_count, line_count);
      }
      data_count++;
    }
  }

  // negedge href
  if (prev_io_post_isp_bus_frame_href && !top->io_post_isp_bus_frame_href) {
    line_count++;
    hcnt = data_count;
    data_count = 0;
  }

  // negedge vsync
  if (prev_io_post_isp_bus_frame_vsync && !top->io_post_isp_bus_frame_vsync && line_count > 0) {
    //output mode
    if (post_isp_mode == ISP_MODE_YCBCR422 || post_isp_mode == ISP_MODE_YCBCR420 || post_isp_mode == ISP_MODE_YCBCR444) {
      param_file_stream << "Mode: YCbCr422\n";
    } else if (post_isp_mode == ISP_MODE_GRAY) {
      param_file_stream << "Mode: Gray\n";
    } else if (post_isp_mode == ISP_MODE_RGB888) {
      param_file_stream << "Mode: RGB888\n";
    } else if (post_isp_mode == ISP_MODE_RAW_BGGR) {
      param_file_stream << "Mode: RAW_BGGR\n";
    }
    param_file_stream << "Height: " << line_count << "\n";
    param_file_stream << "Width: " << hcnt << "\n";
    printf("Height: %d, Width: %d\n", line_count, hcnt);
    csv_file.close();
    param_file_stream.close();
  }

  prev_io_post_isp_bus_frame_vsync = top->io_post_isp_bus_frame_vsync;
  prev_io_post_isp_bus_frame_href = top->io_post_isp_bus_frame_href;
  // prev_io_post_isp_bus_frame_clken = top->io_post_isp_bus_frame_clken;
}

#ifdef USE_DIV
void step_with_div(int n) {
  int clk_div_counter = 0;
  int clk_div_state = 1;

  auto update_clk_div = [&]() {
    if (clk_div_counter == 0) {
      clk_div_state = !clk_div_state;
      clk_div_counter = n - 1;
    } else {
      clk_div_counter--;
    }
    top->clock_div_2 = clk_div_state;
  };

  for (int i = 0; i < n; ++i) {
    top->clock = 1;
    update_clk_div();
    top->eval();
    monitor_output();
#ifdef TRACE
    if (trace_time++ < CONFIG_TRACE_MAX) {
      tfp->dump(contextp->time());
      contextp->timeInc(1);
    }
#endif

    top->clock = 0;
    update_clk_div();
    top->eval();
    monitor_output();
#ifdef TRACE
    if (trace_time++ < CONFIG_TRACE_MAX) {
      tfp->dump(contextp->time());
      contextp->timeInc(1);
    }
#endif
  }
}

void step() {
  step_with_div(CLK_DIV);
}

void step(int n) {
  while (n--) {
    step_with_div(CLK_DIV);
  }
}
#else
void step() {
  top->clock = 1;
  top->eval();
  monitor_output();
#ifdef TRACE
  if (trace_time++ < CONFIG_TRACE_MAX) {
    tfp->dump(contextp->time());
    contextp->timeInc(1);
  }
#endif
  top->clock = 0;
  top->eval();
  monitor_output();
#ifdef TRACE
  if (trace_time++ < CONFIG_TRACE_MAX) {
    tfp->dump(contextp->time());
    contextp->timeInc(1);
  }
#endif
}

void step(int n) {
  while (n--) {
    step();
  }
}
#endif

void reset(int n) {
  top->reset = 1;
  while (n--) {
    step();
  }
  top->reset = 0;
}

void apb_write(int addr, int data) {
  top->io_APB_waddr = addr;
  top->io_APB_wdata = data;
  top->io_APB_wvalid = 1;
  step();
  top->io_APB_wvalid = 0;
  top->io_APB_waddr = 0;
  top->io_APB_wdata = 0;
  step();
}

void sim_image_data(const char* csv_file_path) {
  FILE* file = fopen(csv_file_path, "r");
  if (!file) {
    perror("Failed to open CSV file");
    return;
  }
  apb_write(0xF01F0018, 0x00000000);
  apb_write(0xF01F001C, 0x00000000);
  apb_write(0xF01F0030, 0x00000003);
  int r, g, b;
  int hcnt = 0, vcnt = 0;
  top->io_per_isp_bus_frame_vsync = 1;
  top->io_per_isp_bus_frame_mode = 0;
  #ifdef INPUT_RGB
  while (fscanf(file, "%d,%d,%d", &r, &g, &b) == 3) {
    top->io_per_isp_bus_img_red = r;
    top->io_per_isp_bus_img_green = g;
    top->io_per_isp_bus_img_blue = b;
    top->io_per_isp_bus_frame_href = 1;
    // top->io_per_isp_bus_frame_clken = 1;
    // step();
    // top->io_per_isp_bus_frame_clken = 0;
    step();
    hcnt++;
    if (hcnt == image_width) {
      hcnt = 0;
      vcnt++;
      top->io_per_isp_bus_frame_href = 0;
      step(5);
      printf("End of line[%d]\n", vcnt);
      if (vcnt == image_height) {
        top->io_per_isp_bus_frame_vsync = 0;
        printf("End of image\n");
        step(100000);
        break;
      }
    }
  }
  #else
  // input as raw
  while (fscanf(file, "%d", &r) == 1) {
    top->io_per_isp_bus_img_raw = r;
    top->io_per_isp_bus_frame_href = 1;
    step();
    hcnt++;
    if (hcnt == image_width) {
      hcnt = 0;
      vcnt++;
      top->io_per_isp_bus_frame_href = 0;
      step(5);
      printf("End of line[%d]\n", vcnt);
      if (vcnt == image_height) {
        top->io_per_isp_bus_frame_vsync = 0;
        printf("End of image\n");
        step(100000);
        break;
      }
    }
  }
  #endif
  fclose(file);
}


void load_param_data(const char* param_file_path) {
  FILE* file = fopen(param_file_path, "r");
  if (!file) {
    perror("Failed to open parameter file");
    return;
  }
  char line[256];
  while (fgets(line, sizeof(line), file)) {
    if (sscanf(line, "Height: %d", &image_height) == 1) {
      printf("Image height: %d\n", image_height);
      continue;
    }
    if (sscanf(line, "Width: %d", &image_width) == 1) {
      printf("Image width: %d\n", image_width);
      continue;
    }
  }
  fclose(file);
}

static int parse_args(int argc, char* argv[]) {
  const struct option table[] = {
      {"log", required_argument, NULL, 'l'},
      {"csv", required_argument, NULL, 'c'},
      {"param", required_argument, NULL, 'p'},
      {"output", required_argument, NULL, 'o'},
      {"target", required_argument, NULL, 't'},
      {"help", no_argument, NULL, 'h'},
      {0, 0, NULL, 0},
  };
  int o;
  while ((o = getopt_long(argc, argv, "-hl:c:p:o:t:", table, NULL)) != -1) {
    switch (o) {
      case 'l':
        // log_file = optarg;
        break;
      case 'd':
        break;
      case 'c':
        image_file = optarg;
        break;
      case 'p':
        param_file = optarg;
        break;
      case 'o':
        output_dir = optarg;
        break;
      case 't':
        output_name = optarg;
        break;
      default:
        printf("Usage: %s [OPTION...] IMAGE [args]\n\n", argv[0]);
        printf("\t-b,--batch              run with batch mode\n");
        printf("\t-l,--log=FILE           output log to FILE\n");
        printf("\t-e,--elf=FILE           load elf FILE\n");
        printf(
            "\t-d,--diff=REF_SO        run DiffTest with reference REF_SO\n");
        printf("\t-p,--port=PORT          run DiffTest with port PORT\n");
        printf("\t-o,--output=DIR         specify output directory\n");
        printf("\t-t,--target=NAME         specify output name\n");
        printf("\n");
        exit(0);
    }
  }
  return 0;
}

int main(int argc, char* argv[]) {
  contextp = new VerilatedContext;
  contextp->commandArgs(argc, argv);
  top = new VISP;
  parse_args(argc, argv);
#ifdef TRACE
  tfp = new VerilatedFstC;
  contextp->traceEverOn(true);
  top->trace(tfp, 0);
  tfp->open("wave.fst");
#endif
  reset(10);
  if (image_file == NULL) {
    printf("Please specify the image file using -c option\n");
#ifdef TRACE
    tfp->close();
    delete tfp;
#endif
    delete top;
    delete contextp;
    return 0;
  }
  if (param_file == NULL) {
    printf("Please specify the param file using -p option\n");
  }
  if (output_dir == NULL) {
    printf("Please specify the output directory using -o option\n");
    return 0;
  }

  // Create output files
  std::string csv_file_path =
      std::string(output_dir) + "/" + std::string(output_name) + ".csv";
  std::string param_file_path =
      std::string(output_dir) + "/" + std::string(output_name) + ".txt";
  csv_file.open(csv_file_path);
  param_file_stream.open(param_file_path);

  // Load image data from CSV file

  load_param_data(param_file);
  sim_image_data(image_file);

  // Run the simulation for a specified number of cycles
  for (int i = 0; i < 100; ++i) {
    step();
  }
#ifdef TRACE
  tfp->close();
  delete tfp;
#endif
  delete top;
  delete contextp;
  // printf("argc=%d, argv[0]=%s\n", argc, argv[0]);
  return 0;
}