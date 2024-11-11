from PIL import Image
import numpy as np

def image_to_mif(image_path, mif_path):
    img = Image.open(image_path)
    img = img.convert('RGB')
    
    width, height = img.size
    
    mif_data = []
    
    # Iterate over each pixel
    for y in range(height):
        for x in range(width):
            r, g, b = img.getpixel((x, y))
            rgb888 = (r << 16) | (g << 8) | b
            mif_data.append(rgb888)
    
    # Write MIF file
    with open(mif_path, 'w') as mif_file:
        mif_file.write("DEPTH = {};\n".format(width * height))
        mif_file.write("WIDTH = 24;\n")
        mif_file.write("ADDRESS_RADIX = HEX;\n")
        mif_file.write("DATA_RADIX = HEX;\n")
        mif_file.write("CONTENT BEGIN\n")
        
        for i, data in enumerate(mif_data):
            mif_file.write("{:X} : {:06X};\n".format(i, data))
        
        mif_file.write("END;\n")

if __name__ == "__main__":
    image_path = 'input_image.png'  
    mif_path = 'output_image.mif'   
    image_to_mif(image_path, mif_path)