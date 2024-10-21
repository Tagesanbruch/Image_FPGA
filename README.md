# Verilog图像处理仿真框架

框架使用Verilator进行仿真，使用gtkwave观察波形

## 环境准备

安装环境：Ubuntu 20.04 LTS

### 安装需要的软件
```bash
apt install verilator gtkwave
```

### python环境搭建

```bash
python -m venv venv
source ./venv/bin/activate
```

确认进入`venv`虚拟环境后（命令行左侧出现`(venv)`），安装需要的python包
```
pip3 install numpy matplotlib opencv-python
```

## 基本用法
`vsrc`下存放所有待仿真verilog源文件，顶层模块命名为`TOP`，接口参考如下：
```verilog
module TOP
(
	//global clock
	input				clk,  				//cmos video pixel clock
	input				rst_n,				//global reset

	//Image data prepred to be processd
	input				per_frame_vsync,	//Prepared Image data vsync valid signal
	input				per_frame_href,		//Prepared Image data href vaild  signal
	input				per_frame_clken,	//Prepared Image data output/capture enable clock
	input		[7:0]	per_img_red,		//Prepared Image red data to be processed
	input		[7:0]	per_img_green,		//Prepared Image green data to be processed
	input		[7:0]	per_img_blue,		//Prepared Image blue data to be processed

	//Image data has been processd
	output				post_frame_vsync,	//Processed Image data vsync valid signal
	output				post_frame_href,	//Processed Image data href vaild  signal
	output				post_frame_clken,	//Processed Image data output/capture enable clock
	output		[7:0]	post_img_Y,			//Processed Image brightness output
	output		[7:0]	post_img_Cb,			//Processed Image blue shading output
	output		[7:0]	post_img_Cr			//Processed Image red shading output
);
```

在inputImage中准备好待处理的图像（*jpg格式*），在工程目录下执行`make preprocess`，工程会将图像转化为csv像素文件（RGB888格式）及图像对应参数txt文件，存放在`tempFile/originalImage_csv`目录下

执行`make run TEST_TARGET=<待仿真文件名>`（文件名不需要带.jpg后缀，此项无输入默认为lena20k，即执行`make run`等效为`make run TEST_TARGET=lena20k`），verilator将对工程进行仿真，仿真逻辑实现参考`csrc/sim_main.cpp`，处理结构存放在`tempFile/`仿真波形存放在`wave.vcd`中（注：参考工程仿真621419周期的vcd文件占用52.26MB，应合理设置trace参数，可通过`CONFIG_TRACE_MAX`配置最大记录周期数），通过`make gtkwave`可查看仿真波形

执行`make verify`，工程会将处理好的csv文件转为*png图像*于`outputImage`目录下