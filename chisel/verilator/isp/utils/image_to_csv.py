import cv2
import csv
import os
import argparse
import sys
import numpy as np

parser = argparse.ArgumentParser(description='Process images and save RGB values to CSV and dimensions to TXT.')
parser.add_argument('-i', '--input_dir', required=True, help='Input directory containing .jpg files')
parser.add_argument('-o', '--output_dir', default='../csv/originalImage', help='Output directory for CSV and TXT files')
args = parser.parse_args()

input_directory = args.input_dir
output_directory = args.output_dir

def convert_to_bayer_bggr(image):
    bayer_bggr = np.zeros_like(image)
    
    bayer_bggr[0::2, 0::2, 0] = image[0::2, 0::2, 0]  # B channel
    bayer_bggr[1::2, 0::2, 1] = image[1::2, 0::2, 1]  # G channel
    bayer_bggr[0::2, 1::2, 1] = image[0::2, 1::2, 1]  # G channel
    bayer_bggr[1::2, 1::2, 2] = image[1::2, 1::2, 2]  # R channel
    
    return bayer_bggr

def convert_to_bayer_gbrg(image):
    bayer_gbrg = np.zeros_like(image)

    bayer_gbrg[0::2, 0::2, 1] = image[0::2, 0::2, 1]  # G channel
    bayer_gbrg[1::2, 0::2, 0] = image[1::2, 0::2, 0]  # B channel
    bayer_gbrg[0::2, 1::2, 2] = image[0::2, 1::2, 2]  # R channel
    bayer_gbrg[1::2, 1::2, 1] = image[1::2, 1::2, 1]  # G channel
    return bayer_gbrg

def bggr_save_csv(image, output_path):
    with open(output_path, 'w', newline='') as csvfile:
        height, width, _ = image.shape
        writer = csv.writer(csvfile)
        for y in range(height):
            for x in range(width):
                r, g, b = image[y, x]
                if y % 2 == 0 and x % 2 == 0:
                    writer.writerow([r])
                elif y % 2 == 1 and x % 2 == 1:
                    writer.writerow([b])
                else:
                    writer.writerow([g])

def gbrg_save_csv(image, output_path):
    with open(output_path, 'w', newline='') as csvfile:
        height, width, _ = image.shape
        writer = csv.writer(csvfile)
        for y in range(height):
            for x in range(width):
                r, g, b = image[y, x]
                if y % 2 == 0 and x % 2 == 1:
                    writer.writerow([b])
                elif y % 2 == 1 and x % 2 == 0:
                    writer.writerow([r])
                else:
                    writer.writerow([g])

if not os.path.exists(input_directory):
    print(f"Error: Input directory '{input_directory}' does not exist.")
    sys.exit(1)
os.makedirs(output_directory, exist_ok=True)
image_files = [f for f in os.listdir(input_directory) if f.endswith('.jpg')]

for image_file in image_files:
    image_path = os.path.join(input_directory, image_file)
    image = cv2.imread(image_path)
    height, width, _ = image.shape
    bayer_bggr_image = convert_to_bayer_bggr(image)
    # bayer_bggr_image = convert_to_bayer_gbrg(image)
    csv_file_path = os.path.join(output_directory, f'{os.path.splitext(image_file)[0]}.csv')
    bggr_pic_path = os.path.join(output_directory, f'{os.path.splitext(image_file)[0]}.png')
    bggr_save_csv(bayer_bggr_image, csv_file_path)
    # gbrg_save_csv(bayer_bggr_image, csv_file_path)
    cv2.imwrite(bggr_pic_path, bayer_bggr_image)

    txt_file_path = os.path.join(output_directory, f'{os.path.splitext(image_file)[0]}.txt')
    with open(txt_file_path, mode='w') as txt_file:
        txt_file.write(f'Height: {height}\n')
        txt_file.write(f'Width: {width}\n')