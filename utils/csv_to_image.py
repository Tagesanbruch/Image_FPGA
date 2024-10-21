import csv
import numpy as np
import matplotlib.pyplot as plt
import os
import argparse

# Parse command-line arguments
parser = argparse.ArgumentParser(description='Process images and save RGB values to CSV and dimensions to TXT.')
parser.add_argument('-i', '--input_dir', required=True, help='Input directory containing .csv and .txt files')
parser.add_argument('-o', '--output_dir', default='../image/outputImage', help='Output directory for grayscale images')
args = parser.parse_args()

input_directory = args.input_dir
output_directory = args.output_dir

def read_params(param_file):
    with open(param_file, 'r') as file:
        lines = file.readlines()
        height = int(lines[0].split(":")[1].strip())
        width = int(lines[1].split(":")[1].strip())
    return width, height

def read_y_data(csv_file):
    y_data = []
    with open(csv_file, 'r') as file:
        reader = csv.reader(file)
        for row in reader:
            y_data.append(int(row[0]))  # 读取Y列数据
    return y_data

def save_grayscale_image(y_data, width, height, output_file):
    y_array = np.array(y_data).reshape((height, width))
    plt.imshow(y_array, cmap='gray')
    plt.axis('off')
    plt.savefig(output_file, bbox_inches='tight', pad_inches=0)

def process_files(input_directory, output_directory):
    for filename in os.listdir(input_directory):
        if filename.endswith('.txt'):
            param_file = os.path.join(input_directory, filename)
            csv_file = os.path.join(input_directory, filename.replace('.txt', '.csv'))
            output_file = os.path.join(output_directory, filename.replace('.txt', '.png'))

            if os.path.exists(csv_file):
                width, height = read_params(param_file)
                y_data = read_y_data(csv_file)
                save_grayscale_image(y_data, width, height, output_file)
                print(f"Processed {csv_file} and saved grayscale image to {output_file}")
            else:
                print(f"CSV file {csv_file} not found for {param_file}")

def main():
    if not os.path.exists(output_directory):
        os.makedirs(output_directory)
    process_files(input_directory, output_directory)

if __name__ == '__main__':
    main()