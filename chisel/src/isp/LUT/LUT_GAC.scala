package LUT
import chisel3._

import chisel3.util._

object GAC_MODE extends Enumeration {
  val GAC_1, GAC_0_2, GAC_0_4, GAC_0_67, GAC_1_5, GAC_2_2, GAC_5 = Value
}

class LUT_GAC extends Module {
  val io = IO(new Bundle {
    val I_mode = Input(UInt(4.W))
    val I_data_0 = Input(UInt(8.W))
    val I_data_1 = Input(UInt(8.W))
    val I_data_2 = Input(UInt(8.W))
    val I_data_3 = Input(UInt(8.W))
    val O_data_0 = Output(UInt(8.W))
    val O_data_1 = Output(UInt(8.W))
    val O_data_2 = Output(UInt(8.W))
    val O_data_3 = Output(UInt(8.W))
  })
  val lut_0_2 = VecInit(Seq(
    0.U(8.W), 84.U(8.W), 96.U(8.W), 104.U(8.W), 111.U(8.W), 116.U(8.W), 120.U(8.W), 124.U(8.W), 127.U(8.W), 130.U(8.W), 133.U(8.W), 135.U(8.W), 138.U(8.W), 140.U(8.W), 142.U(8.W), 144.U(8.W), 
    146.U(8.W), 148.U(8.W), 150.U(8.W), 151.U(8.W), 153.U(8.W), 154.U(8.W), 156.U(8.W), 157.U(8.W), 158.U(8.W), 160.U(8.W), 161.U(8.W), 162.U(8.W), 163.U(8.W), 165.U(8.W), 166.U(8.W), 167.U(8.W), 
    168.U(8.W), 169.U(8.W), 170.U(8.W), 171.U(8.W), 172.U(8.W), 173.U(8.W), 174.U(8.W), 175.U(8.W), 176.U(8.W), 176.U(8.W), 177.U(8.W), 178.U(8.W), 179.U(8.W), 180.U(8.W), 181.U(8.W), 181.U(8.W), 
    182.U(8.W), 183.U(8.W), 184.U(8.W), 184.U(8.W), 185.U(8.W), 186.U(8.W), 186.U(8.W), 187.U(8.W), 188.U(8.W), 188.U(8.W), 189.U(8.W), 190.U(8.W), 190.U(8.W), 191.U(8.W), 192.U(8.W), 192.U(8.W), 
    193.U(8.W), 194.U(8.W), 194.U(8.W), 195.U(8.W), 195.U(8.W), 196.U(8.W), 196.U(8.W), 197.U(8.W), 198.U(8.W), 198.U(8.W), 199.U(8.W), 199.U(8.W), 200.U(8.W), 200.U(8.W), 201.U(8.W), 201.U(8.W), 
    202.U(8.W), 202.U(8.W), 203.U(8.W), 203.U(8.W), 204.U(8.W), 204.U(8.W), 205.U(8.W), 205.U(8.W), 206.U(8.W), 206.U(8.W), 207.U(8.W), 207.U(8.W), 207.U(8.W), 208.U(8.W), 208.U(8.W), 209.U(8.W), 
    209.U(8.W), 210.U(8.W), 210.U(8.W), 211.U(8.W), 211.U(8.W), 211.U(8.W), 212.U(8.W), 212.U(8.W), 213.U(8.W), 213.U(8.W), 213.U(8.W), 214.U(8.W), 214.U(8.W), 215.U(8.W), 215.U(8.W), 215.U(8.W), 
    216.U(8.W), 216.U(8.W), 217.U(8.W), 217.U(8.W), 217.U(8.W), 218.U(8.W), 218.U(8.W), 218.U(8.W), 219.U(8.W), 219.U(8.W), 220.U(8.W), 220.U(8.W), 220.U(8.W), 221.U(8.W), 221.U(8.W), 221.U(8.W), 
    222.U(8.W), 222.U(8.W), 222.U(8.W), 223.U(8.W), 223.U(8.W), 223.U(8.W), 224.U(8.W), 224.U(8.W), 224.U(8.W), 225.U(8.W), 225.U(8.W), 225.U(8.W), 226.U(8.W), 226.U(8.W), 226.U(8.W), 227.U(8.W), 
    227.U(8.W), 227.U(8.W), 228.U(8.W), 228.U(8.W), 228.U(8.W), 229.U(8.W), 229.U(8.W), 229.U(8.W), 229.U(8.W), 230.U(8.W), 230.U(8.W), 230.U(8.W), 231.U(8.W), 231.U(8.W), 231.U(8.W), 232.U(8.W), 
    232.U(8.W), 232.U(8.W), 232.U(8.W), 233.U(8.W), 233.U(8.W), 233.U(8.W), 234.U(8.W), 234.U(8.W), 234.U(8.W), 234.U(8.W), 235.U(8.W), 235.U(8.W), 235.U(8.W), 235.U(8.W), 236.U(8.W), 236.U(8.W), 
    236.U(8.W), 237.U(8.W), 237.U(8.W), 237.U(8.W), 237.U(8.W), 238.U(8.W), 238.U(8.W), 238.U(8.W), 238.U(8.W), 239.U(8.W), 239.U(8.W), 239.U(8.W), 239.U(8.W), 240.U(8.W), 240.U(8.W), 240.U(8.W), 
    240.U(8.W), 241.U(8.W), 241.U(8.W), 241.U(8.W), 241.U(8.W), 242.U(8.W), 242.U(8.W), 242.U(8.W), 242.U(8.W), 243.U(8.W), 243.U(8.W), 243.U(8.W), 243.U(8.W), 244.U(8.W), 244.U(8.W), 244.U(8.W), 
    244.U(8.W), 245.U(8.W), 245.U(8.W), 245.U(8.W), 245.U(8.W), 245.U(8.W), 246.U(8.W), 246.U(8.W), 246.U(8.W), 246.U(8.W), 247.U(8.W), 247.U(8.W), 247.U(8.W), 247.U(8.W), 248.U(8.W), 248.U(8.W), 
    248.U(8.W), 248.U(8.W), 248.U(8.W), 249.U(8.W), 249.U(8.W), 249.U(8.W), 249.U(8.W), 250.U(8.W), 250.U(8.W), 250.U(8.W), 250.U(8.W), 250.U(8.W), 251.U(8.W), 251.U(8.W), 251.U(8.W), 251.U(8.W), 
    251.U(8.W), 252.U(8.W), 252.U(8.W), 252.U(8.W), 252.U(8.W), 252.U(8.W), 253.U(8.W), 253.U(8.W), 253.U(8.W), 253.U(8.W), 253.U(8.W), 254.U(8.W), 254.U(8.W), 254.U(8.W), 254.U(8.W), 255.U(8.W), 
  ))
  val lut_0_4 = VecInit(Seq(
    0.U(8.W), 27.U(8.W), 36.U(8.W), 43.U(8.W), 48.U(8.W), 52.U(8.W), 56.U(8.W), 60.U(8.W), 63.U(8.W), 66.U(8.W), 69.U(8.W), 72.U(8.W), 75.U(8.W), 77.U(8.W), 79.U(8.W), 82.U(8.W), 
    84.U(8.W), 86.U(8.W), 88.U(8.W), 90.U(8.W), 92.U(8.W), 93.U(8.W), 95.U(8.W), 97.U(8.W), 99.U(8.W), 100.U(8.W), 102.U(8.W), 103.U(8.W), 105.U(8.W), 106.U(8.W), 108.U(8.W), 109.U(8.W), 
    111.U(8.W), 112.U(8.W), 113.U(8.W), 115.U(8.W), 116.U(8.W), 117.U(8.W), 119.U(8.W), 120.U(8.W), 121.U(8.W), 122.U(8.W), 123.U(8.W), 125.U(8.W), 126.U(8.W), 127.U(8.W), 128.U(8.W), 129.U(8.W), 
    130.U(8.W), 131.U(8.W), 132.U(8.W), 133.U(8.W), 134.U(8.W), 136.U(8.W), 137.U(8.W), 138.U(8.W), 139.U(8.W), 140.U(8.W), 141.U(8.W), 141.U(8.W), 142.U(8.W), 143.U(8.W), 144.U(8.W), 145.U(8.W), 
    146.U(8.W), 147.U(8.W), 148.U(8.W), 149.U(8.W), 150.U(8.W), 151.U(8.W), 152.U(8.W), 152.U(8.W), 153.U(8.W), 154.U(8.W), 155.U(8.W), 156.U(8.W), 157.U(8.W), 157.U(8.W), 158.U(8.W), 159.U(8.W), 
    160.U(8.W), 161.U(8.W), 161.U(8.W), 162.U(8.W), 163.U(8.W), 164.U(8.W), 165.U(8.W), 165.U(8.W), 166.U(8.W), 167.U(8.W), 168.U(8.W), 168.U(8.W), 169.U(8.W), 170.U(8.W), 171.U(8.W), 171.U(8.W), 
    172.U(8.W), 173.U(8.W), 173.U(8.W), 174.U(8.W), 175.U(8.W), 176.U(8.W), 176.U(8.W), 177.U(8.W), 178.U(8.W), 178.U(8.W), 179.U(8.W), 180.U(8.W), 180.U(8.W), 181.U(8.W), 182.U(8.W), 182.U(8.W), 
    183.U(8.W), 184.U(8.W), 184.U(8.W), 185.U(8.W), 186.U(8.W), 186.U(8.W), 187.U(8.W), 187.U(8.W), 188.U(8.W), 189.U(8.W), 189.U(8.W), 190.U(8.W), 191.U(8.W), 191.U(8.W), 192.U(8.W), 192.U(8.W), 
    193.U(8.W), 194.U(8.W), 194.U(8.W), 195.U(8.W), 195.U(8.W), 196.U(8.W), 197.U(8.W), 197.U(8.W), 198.U(8.W), 198.U(8.W), 199.U(8.W), 200.U(8.W), 200.U(8.W), 201.U(8.W), 201.U(8.W), 202.U(8.W), 
    202.U(8.W), 203.U(8.W), 204.U(8.W), 204.U(8.W), 205.U(8.W), 205.U(8.W), 206.U(8.W), 206.U(8.W), 207.U(8.W), 207.U(8.W), 208.U(8.W), 208.U(8.W), 209.U(8.W), 210.U(8.W), 210.U(8.W), 211.U(8.W), 
    211.U(8.W), 212.U(8.W), 212.U(8.W), 213.U(8.W), 213.U(8.W), 214.U(8.W), 214.U(8.W), 215.U(8.W), 215.U(8.W), 216.U(8.W), 216.U(8.W), 217.U(8.W), 217.U(8.W), 218.U(8.W), 218.U(8.W), 219.U(8.W), 
    219.U(8.W), 220.U(8.W), 220.U(8.W), 221.U(8.W), 221.U(8.W), 222.U(8.W), 222.U(8.W), 223.U(8.W), 223.U(8.W), 224.U(8.W), 224.U(8.W), 225.U(8.W), 225.U(8.W), 226.U(8.W), 226.U(8.W), 227.U(8.W), 
    227.U(8.W), 228.U(8.W), 228.U(8.W), 229.U(8.W), 229.U(8.W), 229.U(8.W), 230.U(8.W), 230.U(8.W), 231.U(8.W), 231.U(8.W), 232.U(8.W), 232.U(8.W), 233.U(8.W), 233.U(8.W), 234.U(8.W), 234.U(8.W), 
    235.U(8.W), 235.U(8.W), 235.U(8.W), 236.U(8.W), 236.U(8.W), 237.U(8.W), 237.U(8.W), 238.U(8.W), 238.U(8.W), 239.U(8.W), 239.U(8.W), 239.U(8.W), 240.U(8.W), 240.U(8.W), 241.U(8.W), 241.U(8.W), 
    242.U(8.W), 242.U(8.W), 242.U(8.W), 243.U(8.W), 243.U(8.W), 244.U(8.W), 244.U(8.W), 245.U(8.W), 245.U(8.W), 245.U(8.W), 246.U(8.W), 246.U(8.W), 247.U(8.W), 247.U(8.W), 248.U(8.W), 248.U(8.W), 
    248.U(8.W), 249.U(8.W), 249.U(8.W), 250.U(8.W), 250.U(8.W), 250.U(8.W), 251.U(8.W), 251.U(8.W), 252.U(8.W), 252.U(8.W), 252.U(8.W), 253.U(8.W), 253.U(8.W), 254.U(8.W), 254.U(8.W), 255.U(8.W), 
  ))
  val lut_0_67 = VecInit(Seq(
    0.U(8.W), 6.U(8.W), 9.U(8.W), 12.U(8.W), 15.U(8.W), 18.U(8.W), 20.U(8.W), 22.U(8.W), 25.U(8.W), 27.U(8.W), 29.U(8.W), 31.U(8.W), 32.U(8.W), 34.U(8.W), 36.U(8.W), 38.U(8.W), 
    39.U(8.W), 41.U(8.W), 43.U(8.W), 44.U(8.W), 46.U(8.W), 47.U(8.W), 49.U(8.W), 50.U(8.W), 52.U(8.W), 53.U(8.W), 55.U(8.W), 56.U(8.W), 58.U(8.W), 59.U(8.W), 60.U(8.W), 62.U(8.W), 
    63.U(8.W), 64.U(8.W), 66.U(8.W), 67.U(8.W), 68.U(8.W), 69.U(8.W), 71.U(8.W), 72.U(8.W), 73.U(8.W), 74.U(8.W), 76.U(8.W), 77.U(8.W), 78.U(8.W), 79.U(8.W), 80.U(8.W), 82.U(8.W), 
    83.U(8.W), 84.U(8.W), 85.U(8.W), 86.U(8.W), 87.U(8.W), 89.U(8.W), 90.U(8.W), 91.U(8.W), 92.U(8.W), 93.U(8.W), 94.U(8.W), 95.U(8.W), 96.U(8.W), 97.U(8.W), 98.U(8.W), 99.U(8.W), 
    100.U(8.W), 102.U(8.W), 103.U(8.W), 104.U(8.W), 105.U(8.W), 106.U(8.W), 107.U(8.W), 108.U(8.W), 109.U(8.W), 110.U(8.W), 111.U(8.W), 112.U(8.W), 113.U(8.W), 114.U(8.W), 115.U(8.W), 116.U(8.W), 
    117.U(8.W), 118.U(8.W), 119.U(8.W), 120.U(8.W), 121.U(8.W), 122.U(8.W), 123.U(8.W), 124.U(8.W), 125.U(8.W), 125.U(8.W), 126.U(8.W), 127.U(8.W), 128.U(8.W), 129.U(8.W), 130.U(8.W), 131.U(8.W), 
    132.U(8.W), 133.U(8.W), 134.U(8.W), 135.U(8.W), 136.U(8.W), 137.U(8.W), 138.U(8.W), 138.U(8.W), 139.U(8.W), 140.U(8.W), 141.U(8.W), 142.U(8.W), 143.U(8.W), 144.U(8.W), 145.U(8.W), 146.U(8.W), 
    146.U(8.W), 147.U(8.W), 148.U(8.W), 149.U(8.W), 150.U(8.W), 151.U(8.W), 152.U(8.W), 153.U(8.W), 153.U(8.W), 154.U(8.W), 155.U(8.W), 156.U(8.W), 157.U(8.W), 158.U(8.W), 159.U(8.W), 159.U(8.W), 
    160.U(8.W), 161.U(8.W), 162.U(8.W), 163.U(8.W), 164.U(8.W), 164.U(8.W), 165.U(8.W), 166.U(8.W), 167.U(8.W), 168.U(8.W), 168.U(8.W), 169.U(8.W), 170.U(8.W), 171.U(8.W), 172.U(8.W), 173.U(8.W), 
    173.U(8.W), 174.U(8.W), 175.U(8.W), 176.U(8.W), 177.U(8.W), 177.U(8.W), 178.U(8.W), 179.U(8.W), 180.U(8.W), 181.U(8.W), 181.U(8.W), 182.U(8.W), 183.U(8.W), 184.U(8.W), 185.U(8.W), 185.U(8.W), 
    186.U(8.W), 187.U(8.W), 188.U(8.W), 188.U(8.W), 189.U(8.W), 190.U(8.W), 191.U(8.W), 192.U(8.W), 192.U(8.W), 193.U(8.W), 194.U(8.W), 195.U(8.W), 195.U(8.W), 196.U(8.W), 197.U(8.W), 198.U(8.W), 
    198.U(8.W), 199.U(8.W), 200.U(8.W), 201.U(8.W), 201.U(8.W), 202.U(8.W), 203.U(8.W), 204.U(8.W), 204.U(8.W), 205.U(8.W), 206.U(8.W), 207.U(8.W), 207.U(8.W), 208.U(8.W), 209.U(8.W), 210.U(8.W), 
    210.U(8.W), 211.U(8.W), 212.U(8.W), 213.U(8.W), 213.U(8.W), 214.U(8.W), 215.U(8.W), 215.U(8.W), 216.U(8.W), 217.U(8.W), 218.U(8.W), 218.U(8.W), 219.U(8.W), 220.U(8.W), 221.U(8.W), 221.U(8.W), 
    222.U(8.W), 223.U(8.W), 223.U(8.W), 224.U(8.W), 225.U(8.W), 226.U(8.W), 226.U(8.W), 227.U(8.W), 228.U(8.W), 228.U(8.W), 229.U(8.W), 230.U(8.W), 230.U(8.W), 231.U(8.W), 232.U(8.W), 233.U(8.W), 
    233.U(8.W), 234.U(8.W), 235.U(8.W), 235.U(8.W), 236.U(8.W), 237.U(8.W), 237.U(8.W), 238.U(8.W), 239.U(8.W), 240.U(8.W), 240.U(8.W), 241.U(8.W), 242.U(8.W), 242.U(8.W), 243.U(8.W), 244.U(8.W), 
    244.U(8.W), 245.U(8.W), 246.U(8.W), 246.U(8.W), 247.U(8.W), 248.U(8.W), 248.U(8.W), 249.U(8.W), 250.U(8.W), 250.U(8.W), 251.U(8.W), 252.U(8.W), 252.U(8.W), 253.U(8.W), 254.U(8.W), 255.U(8.W), 
  ))
  val lut_1 = VecInit(Seq(
    0.U(8.W), 1.U(8.W), 2.U(8.W), 3.U(8.W), 4.U(8.W), 5.U(8.W), 6.U(8.W), 7.U(8.W), 8.U(8.W), 9.U(8.W), 10.U(8.W), 11.U(8.W), 12.U(8.W), 13.U(8.W), 14.U(8.W), 15.U(8.W), 
    16.U(8.W), 17.U(8.W), 18.U(8.W), 19.U(8.W), 20.U(8.W), 21.U(8.W), 22.U(8.W), 23.U(8.W), 24.U(8.W), 25.U(8.W), 26.U(8.W), 27.U(8.W), 28.U(8.W), 29.U(8.W), 30.U(8.W), 31.U(8.W), 
    32.U(8.W), 33.U(8.W), 34.U(8.W), 35.U(8.W), 36.U(8.W), 37.U(8.W), 38.U(8.W), 39.U(8.W), 40.U(8.W), 41.U(8.W), 42.U(8.W), 43.U(8.W), 44.U(8.W), 45.U(8.W), 46.U(8.W), 47.U(8.W), 
    48.U(8.W), 49.U(8.W), 50.U(8.W), 51.U(8.W), 52.U(8.W), 53.U(8.W), 54.U(8.W), 55.U(8.W), 56.U(8.W), 57.U(8.W), 58.U(8.W), 59.U(8.W), 60.U(8.W), 61.U(8.W), 62.U(8.W), 63.U(8.W), 
    64.U(8.W), 65.U(8.W), 66.U(8.W), 67.U(8.W), 68.U(8.W), 69.U(8.W), 70.U(8.W), 71.U(8.W), 72.U(8.W), 73.U(8.W), 74.U(8.W), 75.U(8.W), 76.U(8.W), 77.U(8.W), 78.U(8.W), 79.U(8.W), 
    80.U(8.W), 81.U(8.W), 82.U(8.W), 83.U(8.W), 84.U(8.W), 85.U(8.W), 86.U(8.W), 87.U(8.W), 88.U(8.W), 89.U(8.W), 90.U(8.W), 91.U(8.W), 92.U(8.W), 93.U(8.W), 94.U(8.W), 95.U(8.W), 
    96.U(8.W), 97.U(8.W), 98.U(8.W), 99.U(8.W), 100.U(8.W), 101.U(8.W), 102.U(8.W), 103.U(8.W), 104.U(8.W), 105.U(8.W), 106.U(8.W), 107.U(8.W), 108.U(8.W), 109.U(8.W), 110.U(8.W), 111.U(8.W), 
    112.U(8.W), 113.U(8.W), 114.U(8.W), 115.U(8.W), 116.U(8.W), 117.U(8.W), 118.U(8.W), 119.U(8.W), 120.U(8.W), 121.U(8.W), 122.U(8.W), 123.U(8.W), 124.U(8.W), 125.U(8.W), 126.U(8.W), 127.U(8.W), 
    128.U(8.W), 129.U(8.W), 130.U(8.W), 131.U(8.W), 132.U(8.W), 133.U(8.W), 134.U(8.W), 135.U(8.W), 136.U(8.W), 137.U(8.W), 138.U(8.W), 139.U(8.W), 140.U(8.W), 141.U(8.W), 142.U(8.W), 143.U(8.W), 
    144.U(8.W), 145.U(8.W), 146.U(8.W), 147.U(8.W), 148.U(8.W), 149.U(8.W), 150.U(8.W), 151.U(8.W), 152.U(8.W), 153.U(8.W), 154.U(8.W), 155.U(8.W), 156.U(8.W), 157.U(8.W), 158.U(8.W), 159.U(8.W), 
    160.U(8.W), 161.U(8.W), 162.U(8.W), 163.U(8.W), 164.U(8.W), 165.U(8.W), 166.U(8.W), 167.U(8.W), 168.U(8.W), 169.U(8.W), 170.U(8.W), 171.U(8.W), 172.U(8.W), 173.U(8.W), 174.U(8.W), 175.U(8.W), 
    176.U(8.W), 177.U(8.W), 178.U(8.W), 179.U(8.W), 180.U(8.W), 181.U(8.W), 182.U(8.W), 183.U(8.W), 184.U(8.W), 185.U(8.W), 186.U(8.W), 187.U(8.W), 188.U(8.W), 189.U(8.W), 190.U(8.W), 191.U(8.W), 
    192.U(8.W), 193.U(8.W), 194.U(8.W), 195.U(8.W), 196.U(8.W), 197.U(8.W), 198.U(8.W), 199.U(8.W), 200.U(8.W), 201.U(8.W), 202.U(8.W), 203.U(8.W), 204.U(8.W), 205.U(8.W), 206.U(8.W), 207.U(8.W), 
    208.U(8.W), 209.U(8.W), 210.U(8.W), 211.U(8.W), 212.U(8.W), 213.U(8.W), 214.U(8.W), 215.U(8.W), 216.U(8.W), 217.U(8.W), 218.U(8.W), 219.U(8.W), 220.U(8.W), 221.U(8.W), 222.U(8.W), 223.U(8.W), 
    224.U(8.W), 225.U(8.W), 226.U(8.W), 227.U(8.W), 228.U(8.W), 229.U(8.W), 230.U(8.W), 231.U(8.W), 232.U(8.W), 233.U(8.W), 234.U(8.W), 235.U(8.W), 236.U(8.W), 237.U(8.W), 238.U(8.W), 239.U(8.W), 
    240.U(8.W), 241.U(8.W), 242.U(8.W), 243.U(8.W), 244.U(8.W), 245.U(8.W), 246.U(8.W), 247.U(8.W), 248.U(8.W), 249.U(8.W), 250.U(8.W), 251.U(8.W), 252.U(8.W), 253.U(8.W), 254.U(8.W), 255.U(8.W), 
  ))
  val lut_1_5 = VecInit(Seq(
    0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 2.U(8.W), 2.U(8.W), 2.U(8.W), 3.U(8.W), 3.U(8.W), 
    4.U(8.W), 4.U(8.W), 4.U(8.W), 5.U(8.W), 5.U(8.W), 6.U(8.W), 6.U(8.W), 6.U(8.W), 7.U(8.W), 7.U(8.W), 8.U(8.W), 8.U(8.W), 9.U(8.W), 9.U(8.W), 10.U(8.W), 10.U(8.W), 
    11.U(8.W), 11.U(8.W), 12.U(8.W), 12.U(8.W), 13.U(8.W), 14.U(8.W), 14.U(8.W), 15.U(8.W), 15.U(8.W), 16.U(8.W), 17.U(8.W), 17.U(8.W), 18.U(8.W), 18.U(8.W), 19.U(8.W), 20.U(8.W), 
    20.U(8.W), 21.U(8.W), 22.U(8.W), 22.U(8.W), 23.U(8.W), 24.U(8.W), 24.U(8.W), 25.U(8.W), 26.U(8.W), 26.U(8.W), 27.U(8.W), 28.U(8.W), 29.U(8.W), 29.U(8.W), 30.U(8.W), 31.U(8.W), 
    32.U(8.W), 32.U(8.W), 33.U(8.W), 34.U(8.W), 35.U(8.W), 35.U(8.W), 36.U(8.W), 37.U(8.W), 38.U(8.W), 39.U(8.W), 39.U(8.W), 40.U(8.W), 41.U(8.W), 42.U(8.W), 43.U(8.W), 43.U(8.W), 
    44.U(8.W), 45.U(8.W), 46.U(8.W), 47.U(8.W), 48.U(8.W), 49.U(8.W), 49.U(8.W), 50.U(8.W), 51.U(8.W), 52.U(8.W), 53.U(8.W), 54.U(8.W), 55.U(8.W), 56.U(8.W), 57.U(8.W), 57.U(8.W), 
    58.U(8.W), 59.U(8.W), 60.U(8.W), 61.U(8.W), 62.U(8.W), 63.U(8.W), 64.U(8.W), 65.U(8.W), 66.U(8.W), 67.U(8.W), 68.U(8.W), 69.U(8.W), 70.U(8.W), 71.U(8.W), 72.U(8.W), 73.U(8.W), 
    74.U(8.W), 75.U(8.W), 76.U(8.W), 77.U(8.W), 78.U(8.W), 79.U(8.W), 80.U(8.W), 81.U(8.W), 82.U(8.W), 83.U(8.W), 84.U(8.W), 85.U(8.W), 86.U(8.W), 87.U(8.W), 88.U(8.W), 89.U(8.W), 
    90.U(8.W), 91.U(8.W), 92.U(8.W), 93.U(8.W), 94.U(8.W), 96.U(8.W), 97.U(8.W), 98.U(8.W), 99.U(8.W), 100.U(8.W), 101.U(8.W), 102.U(8.W), 103.U(8.W), 104.U(8.W), 105.U(8.W), 107.U(8.W), 
    108.U(8.W), 109.U(8.W), 110.U(8.W), 111.U(8.W), 112.U(8.W), 113.U(8.W), 115.U(8.W), 116.U(8.W), 117.U(8.W), 118.U(8.W), 119.U(8.W), 120.U(8.W), 122.U(8.W), 123.U(8.W), 124.U(8.W), 125.U(8.W), 
    126.U(8.W), 127.U(8.W), 129.U(8.W), 130.U(8.W), 131.U(8.W), 132.U(8.W), 133.U(8.W), 135.U(8.W), 136.U(8.W), 137.U(8.W), 138.U(8.W), 140.U(8.W), 141.U(8.W), 142.U(8.W), 143.U(8.W), 144.U(8.W), 
    146.U(8.W), 147.U(8.W), 148.U(8.W), 149.U(8.W), 151.U(8.W), 152.U(8.W), 153.U(8.W), 155.U(8.W), 156.U(8.W), 157.U(8.W), 158.U(8.W), 160.U(8.W), 161.U(8.W), 162.U(8.W), 164.U(8.W), 165.U(8.W), 
    166.U(8.W), 167.U(8.W), 169.U(8.W), 170.U(8.W), 171.U(8.W), 173.U(8.W), 174.U(8.W), 175.U(8.W), 177.U(8.W), 178.U(8.W), 179.U(8.W), 181.U(8.W), 182.U(8.W), 183.U(8.W), 185.U(8.W), 186.U(8.W), 
    187.U(8.W), 189.U(8.W), 190.U(8.W), 191.U(8.W), 193.U(8.W), 194.U(8.W), 196.U(8.W), 197.U(8.W), 198.U(8.W), 200.U(8.W), 201.U(8.W), 202.U(8.W), 204.U(8.W), 205.U(8.W), 207.U(8.W), 208.U(8.W), 
    209.U(8.W), 211.U(8.W), 212.U(8.W), 214.U(8.W), 215.U(8.W), 217.U(8.W), 218.U(8.W), 219.U(8.W), 221.U(8.W), 222.U(8.W), 224.U(8.W), 225.U(8.W), 227.U(8.W), 228.U(8.W), 229.U(8.W), 231.U(8.W), 
    232.U(8.W), 234.U(8.W), 235.U(8.W), 237.U(8.W), 238.U(8.W), 240.U(8.W), 241.U(8.W), 243.U(8.W), 244.U(8.W), 246.U(8.W), 247.U(8.W), 249.U(8.W), 250.U(8.W), 252.U(8.W), 253.U(8.W), 255.U(8.W), 
  ))
  val lut_2_2 = VecInit(Seq(
    0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 
    0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 2.U(8.W), 2.U(8.W), 2.U(8.W), 
    2.U(8.W), 2.U(8.W), 3.U(8.W), 3.U(8.W), 3.U(8.W), 3.U(8.W), 3.U(8.W), 4.U(8.W), 4.U(8.W), 4.U(8.W), 4.U(8.W), 5.U(8.W), 5.U(8.W), 5.U(8.W), 5.U(8.W), 6.U(8.W), 
    6.U(8.W), 6.U(8.W), 7.U(8.W), 7.U(8.W), 7.U(8.W), 8.U(8.W), 8.U(8.W), 8.U(8.W), 9.U(8.W), 9.U(8.W), 9.U(8.W), 10.U(8.W), 10.U(8.W), 10.U(8.W), 11.U(8.W), 11.U(8.W), 
    12.U(8.W), 12.U(8.W), 13.U(8.W), 13.U(8.W), 13.U(8.W), 14.U(8.W), 14.U(8.W), 15.U(8.W), 15.U(8.W), 16.U(8.W), 16.U(8.W), 17.U(8.W), 17.U(8.W), 18.U(8.W), 18.U(8.W), 19.U(8.W), 
    19.U(8.W), 20.U(8.W), 21.U(8.W), 21.U(8.W), 22.U(8.W), 22.U(8.W), 23.U(8.W), 23.U(8.W), 24.U(8.W), 25.U(8.W), 25.U(8.W), 26.U(8.W), 27.U(8.W), 27.U(8.W), 28.U(8.W), 29.U(8.W), 
    29.U(8.W), 30.U(8.W), 31.U(8.W), 31.U(8.W), 32.U(8.W), 33.U(8.W), 33.U(8.W), 34.U(8.W), 35.U(8.W), 36.U(8.W), 36.U(8.W), 37.U(8.W), 38.U(8.W), 39.U(8.W), 40.U(8.W), 40.U(8.W), 
    41.U(8.W), 42.U(8.W), 43.U(8.W), 44.U(8.W), 45.U(8.W), 45.U(8.W), 46.U(8.W), 47.U(8.W), 48.U(8.W), 49.U(8.W), 50.U(8.W), 51.U(8.W), 52.U(8.W), 53.U(8.W), 54.U(8.W), 55.U(8.W), 
    55.U(8.W), 56.U(8.W), 57.U(8.W), 58.U(8.W), 59.U(8.W), 60.U(8.W), 61.U(8.W), 62.U(8.W), 63.U(8.W), 65.U(8.W), 66.U(8.W), 67.U(8.W), 68.U(8.W), 69.U(8.W), 70.U(8.W), 71.U(8.W), 
    72.U(8.W), 73.U(8.W), 74.U(8.W), 75.U(8.W), 77.U(8.W), 78.U(8.W), 79.U(8.W), 80.U(8.W), 81.U(8.W), 82.U(8.W), 84.U(8.W), 85.U(8.W), 86.U(8.W), 87.U(8.W), 88.U(8.W), 90.U(8.W), 
    91.U(8.W), 92.U(8.W), 93.U(8.W), 95.U(8.W), 96.U(8.W), 97.U(8.W), 99.U(8.W), 100.U(8.W), 101.U(8.W), 103.U(8.W), 104.U(8.W), 105.U(8.W), 107.U(8.W), 108.U(8.W), 109.U(8.W), 111.U(8.W), 
    112.U(8.W), 114.U(8.W), 115.U(8.W), 117.U(8.W), 118.U(8.W), 119.U(8.W), 121.U(8.W), 122.U(8.W), 124.U(8.W), 125.U(8.W), 127.U(8.W), 128.U(8.W), 130.U(8.W), 131.U(8.W), 133.U(8.W), 135.U(8.W), 
    136.U(8.W), 138.U(8.W), 139.U(8.W), 141.U(8.W), 142.U(8.W), 144.U(8.W), 146.U(8.W), 147.U(8.W), 149.U(8.W), 151.U(8.W), 152.U(8.W), 154.U(8.W), 156.U(8.W), 157.U(8.W), 159.U(8.W), 161.U(8.W), 
    162.U(8.W), 164.U(8.W), 166.U(8.W), 168.U(8.W), 169.U(8.W), 171.U(8.W), 173.U(8.W), 175.U(8.W), 176.U(8.W), 178.U(8.W), 180.U(8.W), 182.U(8.W), 184.U(8.W), 186.U(8.W), 187.U(8.W), 189.U(8.W), 
    191.U(8.W), 193.U(8.W), 195.U(8.W), 197.U(8.W), 199.U(8.W), 201.U(8.W), 203.U(8.W), 205.U(8.W), 207.U(8.W), 209.U(8.W), 211.U(8.W), 213.U(8.W), 215.U(8.W), 217.U(8.W), 219.U(8.W), 221.U(8.W), 
    223.U(8.W), 225.U(8.W), 227.U(8.W), 229.U(8.W), 231.U(8.W), 233.U(8.W), 235.U(8.W), 237.U(8.W), 239.U(8.W), 241.U(8.W), 244.U(8.W), 246.U(8.W), 248.U(8.W), 250.U(8.W), 252.U(8.W), 255.U(8.W), 
  ))
  val lut_5 = VecInit(Seq(
    0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 
    0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 
    0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 
    0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 
    0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 
    0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 0.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 1.U(8.W), 
    1.U(8.W), 2.U(8.W), 2.U(8.W), 2.U(8.W), 2.U(8.W), 2.U(8.W), 2.U(8.W), 2.U(8.W), 2.U(8.W), 3.U(8.W), 3.U(8.W), 3.U(8.W), 3.U(8.W), 3.U(8.W), 3.U(8.W), 3.U(8.W), 
    4.U(8.W), 4.U(8.W), 4.U(8.W), 4.U(8.W), 4.U(8.W), 5.U(8.W), 5.U(8.W), 5.U(8.W), 5.U(8.W), 6.U(8.W), 6.U(8.W), 6.U(8.W), 6.U(8.W), 7.U(8.W), 7.U(8.W), 7.U(8.W), 
    8.U(8.W), 8.U(8.W), 8.U(8.W), 9.U(8.W), 9.U(8.W), 9.U(8.W), 10.U(8.W), 10.U(8.W), 11.U(8.W), 11.U(8.W), 11.U(8.W), 12.U(8.W), 12.U(8.W), 13.U(8.W), 13.U(8.W), 14.U(8.W), 
    14.U(8.W), 15.U(8.W), 15.U(8.W), 16.U(8.W), 16.U(8.W), 17.U(8.W), 17.U(8.W), 18.U(8.W), 19.U(8.W), 19.U(8.W), 20.U(8.W), 21.U(8.W), 21.U(8.W), 22.U(8.W), 23.U(8.W), 24.U(8.W), 
    24.U(8.W), 25.U(8.W), 26.U(8.W), 27.U(8.W), 28.U(8.W), 28.U(8.W), 29.U(8.W), 30.U(8.W), 31.U(8.W), 32.U(8.W), 33.U(8.W), 34.U(8.W), 35.U(8.W), 36.U(8.W), 37.U(8.W), 38.U(8.W), 
    39.U(8.W), 41.U(8.W), 42.U(8.W), 43.U(8.W), 44.U(8.W), 45.U(8.W), 47.U(8.W), 48.U(8.W), 49.U(8.W), 51.U(8.W), 52.U(8.W), 54.U(8.W), 55.U(8.W), 57.U(8.W), 58.U(8.W), 60.U(8.W), 
    61.U(8.W), 63.U(8.W), 64.U(8.W), 66.U(8.W), 68.U(8.W), 70.U(8.W), 71.U(8.W), 73.U(8.W), 75.U(8.W), 77.U(8.W), 79.U(8.W), 81.U(8.W), 83.U(8.W), 85.U(8.W), 87.U(8.W), 89.U(8.W), 
    92.U(8.W), 94.U(8.W), 96.U(8.W), 98.U(8.W), 101.U(8.W), 103.U(8.W), 106.U(8.W), 108.U(8.W), 111.U(8.W), 113.U(8.W), 116.U(8.W), 119.U(8.W), 121.U(8.W), 124.U(8.W), 127.U(8.W), 130.U(8.W), 
    133.U(8.W), 136.U(8.W), 139.U(8.W), 142.U(8.W), 145.U(8.W), 148.U(8.W), 152.U(8.W), 155.U(8.W), 158.U(8.W), 162.U(8.W), 165.U(8.W), 169.U(8.W), 173.U(8.W), 176.U(8.W), 180.U(8.W), 184.U(8.W), 
    188.U(8.W), 192.U(8.W), 196.U(8.W), 200.U(8.W), 204.U(8.W), 208.U(8.W), 213.U(8.W), 217.U(8.W), 221.U(8.W), 226.U(8.W), 230.U(8.W), 235.U(8.W), 240.U(8.W), 245.U(8.W), 250.U(8.W), 255.U(8.W), 
  ))
  io.O_data_0 := MuxCase(lut_1(io.I_data_0), Seq(
    (io.I_mode === GAC_MODE.GAC_0_2.id.U) -> lut_0_2(io.I_data_0),
    (io.I_mode === GAC_MODE.GAC_0_4.id.U) -> lut_0_4(io.I_data_0),
    (io.I_mode === GAC_MODE.GAC_0_67.id.U) -> lut_0_67(io.I_data_0),
    (io.I_mode === GAC_MODE.GAC_1.id.U) -> lut_1(io.I_data_0),
    (io.I_mode === GAC_MODE.GAC_1_5.id.U) -> lut_1_5(io.I_data_0),
    (io.I_mode === GAC_MODE.GAC_2_2.id.U) -> lut_2_2(io.I_data_0),
    (io.I_mode === GAC_MODE.GAC_5.id.U) -> lut_5(io.I_data_0),
  ))
  io.O_data_1 := MuxCase(lut_1(io.I_data_1), Seq(
    (io.I_mode === GAC_MODE.GAC_0_2.id.U) -> lut_0_2(io.I_data_1),
    (io.I_mode === GAC_MODE.GAC_0_4.id.U) -> lut_0_4(io.I_data_1),
    (io.I_mode === GAC_MODE.GAC_0_67.id.U) -> lut_0_67(io.I_data_1),
    (io.I_mode === GAC_MODE.GAC_1.id.U) -> lut_1(io.I_data_1),
    (io.I_mode === GAC_MODE.GAC_1_5.id.U) -> lut_1_5(io.I_data_1),
    (io.I_mode === GAC_MODE.GAC_2_2.id.U) -> lut_2_2(io.I_data_1),
    (io.I_mode === GAC_MODE.GAC_5.id.U) -> lut_5(io.I_data_1),
  ))
  io.O_data_2 := MuxCase(lut_1(io.I_data_2), Seq(
    (io.I_mode === GAC_MODE.GAC_0_2.id.U) -> lut_0_2(io.I_data_2),
    (io.I_mode === GAC_MODE.GAC_0_4.id.U) -> lut_0_4(io.I_data_2),
    (io.I_mode === GAC_MODE.GAC_0_67.id.U) -> lut_0_67(io.I_data_2),
    (io.I_mode === GAC_MODE.GAC_1.id.U) -> lut_1(io.I_data_2),
    (io.I_mode === GAC_MODE.GAC_1_5.id.U) -> lut_1_5(io.I_data_2),
    (io.I_mode === GAC_MODE.GAC_2_2.id.U) -> lut_2_2(io.I_data_2),
    (io.I_mode === GAC_MODE.GAC_5.id.U) -> lut_5(io.I_data_2),
  ))
  io.O_data_3 := MuxCase(lut_1(io.I_data_3), Seq(
    (io.I_mode === GAC_MODE.GAC_0_2.id.U) -> lut_0_2(io.I_data_3),
    (io.I_mode === GAC_MODE.GAC_0_4.id.U) -> lut_0_4(io.I_data_3),
    (io.I_mode === GAC_MODE.GAC_0_67.id.U) -> lut_0_67(io.I_data_3),
    (io.I_mode === GAC_MODE.GAC_1.id.U) -> lut_1(io.I_data_3),
    (io.I_mode === GAC_MODE.GAC_1_5.id.U) -> lut_1_5(io.I_data_3),
    (io.I_mode === GAC_MODE.GAC_2_2.id.U) -> lut_2_2(io.I_data_3),
    (io.I_mode === GAC_MODE.GAC_5.id.U) -> lut_5(io.I_data_3),
  ))
}
