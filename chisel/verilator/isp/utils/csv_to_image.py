import csv
import numpy as np
from PIL import Image
import os
import argparse

parser = argparse.ArgumentParser(description='Process images and save RGB values to CSV and dimensions to TXT.')
parser.add_argument('-i', '--input_dir', required=True, help='Input directory containing .csv and .txt files')
parser.add_argument('-o', '--output_dir', default='../image/outputImage', help='Output directory for grayscale images')
args = parser.parse_args()

input_directory = args.input_dir
output_directory = args.output_dir

def read_params(param_file):
    with open(param_file, 'r') as file:
        lines = file.readlines()
        mode = lines[0].split(":")[1].strip()
        height = int(lines[1].split(":")[1].strip())
        width = int(lines[2].split(":")[1].strip())
    return mode, width, height

def read_y_data(csv_file):
    y_data = []
    with open(csv_file, 'r') as file:
        reader = csv.reader(file)
        for row in reader:
            y_data.append(int(row[0]))
    return y_data

def read_ycbcr_data(csv_file):
    ycbcr_data = []
    with open(csv_file, 'r') as file:
        reader = csv.reader(file)
        for row in reader:
            y = int(row[0])
            u = int(row[1])
            v = int(row[2])
            ycbcr_data.append((y, u, v))
    return ycbcr_data

def read_rgb_data(csv_file):
    rgb_data = []
    with open(csv_file, 'r') as file:
        reader = csv.reader(file)
        for row in reader:
            r = int(row[0])
            g = int(row[1])
            b = int(row[2])
            rgb_data.append((r, g, b))
    return rgb_data

def read_raw_bggr_data(csv_file):
    raw_bggr_data = []
    with open(csv_file, 'r') as file:
        reader = csv.reader(file)
        for row in reader:
            raw_bggr_data.append(int(row[0]))
    return raw_bggr_data

def save_grayscale_image(y_data, width, height, output_file):
    y_array = np.array(y_data, dtype=np.uint8).reshape((height, width))
    image = Image.fromarray(y_array, mode='L')
    image.save(output_file)

def save_rgb_image(rgb_data, width, height, output_file):
    rgb_array = np.array(rgb_data, dtype=np.uint8).reshape((height, width, 3))
    image = Image.fromarray(rgb_array, mode='RGB')
    image.save(output_file)

# def save_ycbcr_image(ycbcr_data, width, height, output_file):
#     ycbcr_array = np.array(ycbcr_data, dtype=np.uint8).reshape((height, width, 3))
#     image = Image.fromarray(ycbcr_array, mode='YCbCr')
#     rgb_image = image.convert('RGB')
#     rgb_image.save(output_file)

# /*********************************************
# 	R = 1.164(Y-16) + 1.596(Cr-128)
# 	G = 1.164(Y-16) - 0.391(Cb-128) - 0.813(Cr-128)
# 	B = 1.164(Y-16) + 2.018(Cb-128)
# 	->
# 	R = 1.164Y + 1.596Cr - 222.912
# 	G = 1.164Y - 0.391Cb - 0.813Cr + 135.488
# 	B = 1.164Y + 2.018Cb - 276.928
# 	->
# 	R << 9 = 596Y				+ 	817Cr	-	114131
# 	G << 9 = 596Y	-	200Cb	-	416Cr	+	69370
# 	B << 9 = 596Y	+	1033Cb				-	141787
# **********************************************/

def yuv_to_rgb(y, u, v):
    r = 1.164*y + 1.596*v - 222.912
    g = 1.164*y - 0.391*u - 0.813*v + 135.488
    b = 1.164*y + 2.018*u - 276.928
    #changed to uint 8
    uint8_r = np.uint8(np.clip(r, 0, 255))
    uint8_g = np.uint8(np.clip(g, 0, 255))
    uint8_b = np.uint8(np.clip(b, 0, 255))
    return uint8_r, uint8_g, uint8_b

def save_ycbcr_image(ycbcr_data, width, height, output_file):
    rgb_data = []
    for y, u, v in ycbcr_data:
        r, g, b = yuv_to_rgb(y, u, v)
        rgb_data.append((r, g, b))
    rgb_array = np.array(rgb_data, dtype=np.uint8).reshape((height, width, 3))
    image = Image.fromarray(rgb_array, mode='RGB')
    image.save(output_file)

def save_raw_bggr_image(raw_bggr_data, width, height, output_file):
    hcnt = 0
    vcnt = 0
    rgb_data = []
    for j in range(height):
        for i in range(width):
            if (i % 2 == 0 and j % 2 == 0): # B
                rgb_data.append((0, 0, raw_bggr_data[i + j * width]))
            elif (i % 2 == 1 and j % 2 == 0): # Gb
                rgb_data.append((0, raw_bggr_data[i + j * width], 0))
            elif (i % 2 == 0 and j % 2 == 1): # Gr
                rgb_data.append((0, raw_bggr_data[i + j * width], 0))
            elif (i % 2 == 1 and j % 2 == 1): # R
                rgb_data.append((raw_bggr_data[i + j * width], 0, 0))
    save_rgb_image(rgb_data, width, height, output_file)

def process_files(input_directory, output_directory):
    for filename in os.listdir(input_directory):
        if filename.endswith('.txt'):
            param_file = os.path.join(input_directory, filename)
            csv_file = os.path.join(input_directory, filename.replace('.txt', '.csv'))
            output_file = os.path.join(output_directory, filename.replace('.txt', '.png'))

            if os.path.exists(csv_file):
                mode, width, height = read_params(param_file)
                if mode == "Gray":
                    y_data = read_y_data(csv_file)
                    if len(y_data) == width * height:
                        save_grayscale_image(y_data, width, height, output_file)
                        print(f"Processed {csv_file} and saved grayscale image to {output_file}")
                    else:
                        print(f"Data size mismatch for {csv_file}: expected {width * height}, got {len(y_data)}")
                elif mode == "RGB888":
                    rgb_data = read_rgb_data(csv_file)
                    if len(rgb_data) == width * height:
                        save_rgb_image(rgb_data, width, height, output_file)
                        print(f"Processed {csv_file} and saved RGB image to {output_file}")
                    else:
                        print(f"Data size mismatch for {csv_file}: expected {width * height}, got {len(rgb_data)}")
                elif mode == "RAW_BGGR":
                    raw_bggr_data = read_raw_bggr_data(csv_file)
                    if len(raw_bggr_data) == width * height:
                        save_raw_bggr_image(raw_bggr_data, width, height, output_file)
                        print(f"Processed {csv_file} and saved RAW BGGR image to {output_file}")
                    else:
                        print(f"Data size mismatch for {csv_file}: expected {width * height}, got {len(raw_bggr_data)}")
                elif mode == "YCbCr422":
                    ycbcr_data = read_ycbcr_data(csv_file)
                    if len(ycbcr_data) == width * height:
                        save_ycbcr_image(ycbcr_data, width, height, output_file)
                        print(f"Processed {csv_file} and saved YCbCr image to {output_file}")
                    else:
                        print(f"Data size mismatch for {csv_file}: expected {width * height}, got {len(ycbcr_data)}")
                # y_data = read_y_data(csv_file)
                # if len(y_data) == width * height:
                #     save_grayscale_image(y_data, width, height, output_file)
                #     print(f"Processed {csv_file} and saved grayscale image to {output_file}")
                # else:
                #     print(f"Data size mismatch for {csv_file}: expected {width * height}, got {len(y_data)}")
            else:
                print(f"CSV file {csv_file} not found for {param_file}")

def main():
    if not os.path.exists(output_directory):
        os.makedirs(output_directory)
    process_files(input_directory, output_directory)

if __name__ == '__main__':
    main()