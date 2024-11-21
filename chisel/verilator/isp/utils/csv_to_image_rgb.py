import csv
import numpy as np
from PIL import Image
import os
import argparse

parser = argparse.ArgumentParser(description='Process images and save RGB values to images.')
parser.add_argument('-i', '--input_dir', required=True, help='Input directory containing .csv and .txt files')
parser.add_argument('-o', '--output_dir', default='../image/outputImage', help='Output directory for RGB images')
args = parser.parse_args()

input_directory = args.input_dir
output_directory = args.output_dir

def read_params(param_file):
    with open(param_file, 'r') as file:
        lines = file.readlines()
        height = int(lines[0].split(":")[1].strip())
        width = int(lines[1].split(":")[1].strip())
    return width, height

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

def save_rgb_image(rgb_data, width, height, output_file):
    rgb_array = np.array(rgb_data, dtype=np.uint8).reshape((height, width, 3))
    image = Image.fromarray(rgb_array, mode='RGB')
    image.save(output_file)

def process_files(input_directory, output_directory):
    for filename in os.listdir(input_directory):
        if filename.endswith('.txt'):
            param_file = os.path.join(input_directory, filename)
            csv_file = os.path.join(input_directory, filename.replace('.txt', '.csv'))
            output_file = os.path.join(output_directory, filename.replace('.txt', '.png'))

            if os.path.exists(csv_file):
                width, height = read_params(param_file)
                rgb_data = read_rgb_data(csv_file)
                if len(rgb_data) == width * height:
                    save_rgb_image(rgb_data, width, height, output_file)
                    print(f"Processed {csv_file} and saved RGB image to {output_file}")
                else:
                    print(f"Data size mismatch for {csv_file}: expected {width * height}, got {len(rgb_data)}")
            else:
                print(f"CSV file {csv_file} not found for {param_file}")

def main():
    if not os.path.exists(output_directory):
        os.makedirs(output_directory)
    process_files(input_directory, output_directory)

if __name__ == '__main__':
    main()