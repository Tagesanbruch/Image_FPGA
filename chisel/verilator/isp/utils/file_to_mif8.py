def file_to_mif(input_file, output_file):
    depth = 65536
    width = 8

    with open(input_file, 'rb') as f:
        data = f.read()

    with open(output_file, 'w') as mif:
        mif.write(f"DEPTH = {depth};\n")
        mif.write(f"WIDTH = {width};\n")
        mif.write("ADDRESS_RADIX = HEX;\n")
        mif.write("DATA_RADIX = HEX;\n")
        mif.write("CONTENT BEGIN\n")

        for address in range(depth):
            if address < len(data):
                word = data[address]
                mif.write(f"    {address:04X} : {word:02X};\n")
            else:
                mif.write(f"    {address:04X} : 00;\n")

        mif.write("END;\n")

input_file = 'Scart.jpeg'
output_file = 'output.mif'

file_to_mif(input_file, output_file)