import socket

import hlsvdpro
import numpy as np
import matplotlib.pyplot as plt
from readFromSocket import read_string, read_1DArrayDouble, write_1DArrayDouble, read_int, read_float, write_int

HOST = "localhost"
PORT = 42311
freq0 = [3.82828265e-04, 3.94339386e-02, 3.60897262e-03, 6.45623490e-02, 4.85209932e-02, 7.26164973e-02, 8.97837749e-02,  9.41598483e-02, 1.05887952e-01,  1.30243574e-01, -1.34508469e-04, 1.41866902e-01, 5.92026751e-02, 1.63986620e-01, 1.70866772e-01, 1.54505847e-01, 2.10843967e-01, 2.43686511e-01, 2.59173617e-01, -1.70390382e-01]
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
sock.bind(('', PORT))
sock.listen(5)
client = sock.accept()[0]
order = read_string(client)
if order == 'writeData':
    print("real data")
    realdata = read_1DArrayDouble(client)
    print(realdata)
    print("imag data")
    imagdata = read_1DArrayDouble(client)
    print(imagdata)
    print("nsv_sought")
    nsv_sought = read_int(client)
    print(nsv_sought)
    print("dwell_time")
    dwell_time = read_float(client)
    print(dwell_time)
    print("m")
    m = read_int(client)
    print(m)
order = read_string(client)
if order == 'run':
    data = realdata + 1j * imagdata
    npts = len(data)
    result = hlsvdpro.hlsvd(data, nsv_sought, dwell_time)
    nsv_found, singvals, freq, damp, ampl, phas = result
    print(nsv_found)
    write_int(client, nsv_found)
    write_1DArrayDouble(client,singvals)
    write_1DArrayDouble(client,freq)
    write_1DArrayDouble(client,damp)
    write_1DArrayDouble(client,ampl)
    write_1DArrayDouble(client,phas)



    # print("np.allclose(freq, indat['freq0']) = ", np.allclose(freq, freq0))

    # fid = hlsvdpro.create_hlsvd_fids(result, npts, dwell_time, sum_results=True, convert=False)
    #
    # chop = ((((np.arange(len(fid)) + 1) % 2) * 2) - 1)
    # dat = data * chop
    # fit = fid * chop
    # dat[0] *= 0.5
    # fit[0] *= 0.5
    #
    # plt.plot(np.fft.fft(dat).real, color='r')
    # plt.plot(np.fft.fft(fit).real, color='b')
    # plt.plot(np.fft.fft(dat - fit).real, color='g')
    # plt.show()
