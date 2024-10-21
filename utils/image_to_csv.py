import cv2
import csv
import os
import argparse
import sys

# Parse command-line arguments
parser = argparse.ArgumentParser(description='Process images and save RGB values to CSV and dimensions to TXT.')
parser.add_argument('-i', '--input_dir', required=True, help='Input directory containing .jpg files')
parser.add_argument('-o', '--output_dir', default='../csv/originalImage', help='Output directory for CSV and TXT files')
args = parser.parse_args()

input_directory = args.input_dir
output_directory = args.output_dir

# Check if input directory exists
if not os.path.exists(input_directory):
    print(f"Error: Input directory '{input_directory}' does not exist.")
    sys.exit(1)

# Create output directory if it doesn't exist
os.makedirs(output_directory, exist_ok=True)

# Get a list of all .jpg files in the input directory
image_files = [f for f in os.listdir(input_directory) if f.endswith('.jpg')]

# Process each image file
for image_file in image_files:
    image_path = os.path.join(input_directory, image_file)
    image = cv2.imread(image_path)

    # Get the image dimensions
    height, width, _ = image.shape

    # Create a corresponding CSV file in the output directory
    csv_file_path = os.path.join(output_directory, f'{os.path.splitext(image_file)[0]}.csv')
    with open(csv_file_path, mode='w', newline='') as csv_file:
        csv_writer = csv.writer(csv_file)
        
        # Iterate over each pixel in the image
        for y in range(height):
            for x in range(width):
                # Extract the RGB values
                b, g, r = image[y, x]
                # Write the RGB values to the CSV file
                csv_writer.writerow([r, g, b])

    # Create a corresponding TXT file in the output directory
    txt_file_path = os.path.join(output_directory, f'{os.path.splitext(image_file)[0]}.txt')
    with open(txt_file_path, mode='w') as txt_file:
        # Write the image dimensions to the TXT file
        txt_file.write(f'Height: {height}\n')
        txt_file.write(f'Width: {width}\n')