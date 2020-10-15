# #############################################################################
# server demo
#
# it will work for any number of clients at a time
# socket is created for each client and each client is served in a new thread until it sends message "FINISHED"
# the server is closed when last client disconnects
#
#
# 1. establishes a socket on a server side and bind it to an agreed port (the same port will be used on a client side)
#
# infinitive loop for connecting with a new client (ends )
# 2. the socket blocks and waits for an incoming connection until a client connects with the server
#    Once a client connects with the server (e.g. in Java a Socket object is created with the corresponding port ),
#    a new socket representing the connection with the client is obtained and server stops  blocking the process
#
# loop for reading until message 'FINSIHED' is received
# 3. The new socket waits for data and once arrive it reads
#           -string message (preceded by an integer specifying its length ),
#            if message 'FINSIHED' is received, connection with the client is closed and
#            the program returns to point 2. (the server socket blocks and waits for a new incoming connection....)
#           -double array of real components (preceded by an integer specifying its length )
#           -double array of imaginary components (preceded by an integer specifying its length )

#  4. Imaginary component is multiplied by -1

#  5.  - sends
#           double array of real components
#           double array of imaginary components
#     end of loop for reading
# end of loop for connection
#
# Authors: Jana Starcukova, jana@isibrno.cz
# Date:    2020
# #############################################################################



import socket
import sys
import time
import threading
import select
from readFromSocket import read_string, read_1DArrayDouble, write_1DArrayDouble, read_int, read_float, write_int
import hlsvdpro


# #############################################################################

class count():
    nClients =0
    def __init__( self):
        pass

class ClientThread(threading.Thread):

    def __init__( self, client,sock):
        self.client  = client
        self.sock    = sock
        threading.Thread.__init__( self )
        print ("client started")
        count.nClients+=1
    #_enddef


    def run (self):

        # 1. read data from the socket received from the client until message 'FINISHED' is received
        try:
            print('waiting for data' )
            order = read_string(self.client)
            while  order != 'FINISHED':

                print(order)
                if order == 'writeData':
                    print("real data")
                    realdata = read_1DArrayDouble(self.client)
                    print(realdata)
                    print("imag data")
                    imagdata = read_1DArrayDouble(self.client)
                    print(imagdata)
                    print("nsv_sought")
                    nsv_sought = read_int(self.client)
                    print(nsv_sought)
                    print("dwell_time")
                    dwell_time = read_float(self.client)
                    print(dwell_time)
                    print("m")
                    m = read_int(self.client)
                    print(m)
                order = read_string(self.client)
                if order == 'run':
                    data = realdata + 1j * imagdata
                    npts = len(data)
                    result = hlsvdpro.hlsvd(data, nsv_sought, dwell_time)
                    nsv_found, singvals, freq, damp, ampl, phas = result
                    print(nsv_found)
                    write_int(self.client, nsv_found)
                    write_1DArrayDouble(self.client,singvals)
                    write_1DArrayDouble(self.client,freq)
                    write_1DArrayDouble(self.client,damp)
                    write_1DArrayDouble(self.client,ampl)
                    write_1DArrayDouble(self.client,phas)

                    fid = hlsvdpro.create_hlsvd_fids(result, npts, dwell_time, sum_results=True, convert=False)
                    # chop = ((((np.arange(len(fid)) + 1) % 2) * 2) - 1)
                    # dat = data * chop
                    # fit = fid * chop
                    # dat[0] *= 0.5
                    # fit[0] *= 0.5
                    print(data - fid)
                    # plt.plot(np.fft.fft(dat).real, color='r')
                    # plt.plot(np.fft.fft(fit).real, color='b')
                    # plt.plot(np.fft.fft(dat - fit).real, color='g')
                    # plt.show()
        except Exception as err:   #    except ConnectionResetError: in python 3...
            print('Exception caught: %s\nClosing...' % err)
         # ends connection with the client, waits for a need connection (client = sock.accept()[0] )
        print ('thread closing\n')
        self.client.close()
        count.nClients-=1


    #_enddef
#_endclass (ProtocolThread)


def accept(sock):
    client = sock.accept()[0]
    # create and start a new thread that will be handle connection with the new client
    ClientThread (client,sock).start()
    time.sleep(.1)

def main (port):


    # The steps involved in establishing a socket on the server side:
    # Create a socket with the socket() system call.
    # SOCK_STREAM - delivery is guaranteed
    # If you send through the stream socket three items "A, B, C", they will arrive
    # in the same order "A, B, C",
    # These sockets use TCP (Transmission Control  Protocol) for data transmission.
    # If delivery is impossible, the sender receives
    # an error indicator. Data records do not have any boundaries.

    sock = socket.socket (socket.AF_INET, socket.SOCK_STREAM)


    # setting options at the socket level,
    # SO_REUSEADDR specifies that the rules used in validating addresses supplied
    # to bind() should allow reuse of local addresses, if this is supported by
    # the protocol

    sock.setsockopt (socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

    # Bind the socket to an address using the bind() system call.
    # For a server socket on the Internet, an address consists of a port number
    # on the host machine.
    # A pair (host, port) is used for the AF_INET address family
    # host address: '' represents INADDR_ANY, which is used to bind to all interfaces
    HOST = "localhost"
    PORT = 42311
    sock.bind ((HOST, PORT))

    # Listen for incoming connections.
    # the argument to listen tells the socket library that we want it to queue up
    # as many as 5 connect requests (the normal max) before refusing outside
    # connections.

    sock.listen( 5 )


    # infinitive loop (this is not the best solution but the simplest)


    try:
        print ("Server is listening for incoming connections on port {}".format(port))
        # accept() blocks and waits for an incoming connection
        # Accept a connection with the accept() system call.
        # This call typically blocks the connection until a client connects with the server.

        accept(sock)
        print("connected")
        while count.nClients!=0  :
            # Returns readable sockets.
            # If the readable socket is the main server socket (the one being used to listen for connections),
            # then the 'readable' condition means the server is ready to accept another incoming connection.
            # Sets also the client socket to not block.

            readable, out, ex = select.select([sock], [], [],5)

            for s in readable:
                if s is sock:
                    accept(sock)

        # end of while
    except Exception as err:   ##    except ConnectionResetError: in python 3...
        print('Exception caught: %s\nClosing...' % err)
        # ends connection with the client, waits for a need connection (client = sock.accept()[0] )

    sock.close()
    print ('server closed' )
# #############################################################################

if "__main__" == __name__:

    # port can be read from a command line
    port=42312  # default value
    if len(sys.argv)>1:
        try:
            port = (int) (sys.argv[1])
        except ValueError:
            print ("Wrong syntax of the input argument ({}) in nmrscopeb batch file.\nPort number must be integer. Default port {} will be used instead.".format(sys.argv[1],port))
        #endtry
    main(port)
    print("Terminated\n")


#_endif


# #############################################################################
