def file_to_mif(input_file, output_file):
    depth = 8192
    width = 32

    with open(input_file, 'rb') as f:
        data = f.read()

    with open(output_file, 'w') as mif:
        mif.write(f"DEPTH = {depth};\n")
        mif.write(f"WIDTH = {width};\n")
        mif.write("ADDRESS_RADIX = HEX;\n")
        mif.write("DATA_RADIX = HEX;\n")
        mif.write("CONTENT BEGIN\n")

        for address in range(depth):
            if address * 4 < len(data):
                word = int.from_bytes(data[address * 4:address * 4 + 4], byteorder='big', signed=False)
                mif.write(f"    {address:04X} : {word:08X};\n")
            else:
                mif.write(f"    {address:04X} : 00000000;\n")

        mif.write("END;\n")

input_file = 'Scart.jpeg'
output_file = 'output.mif'

file_to_mif(input_file, output_file)