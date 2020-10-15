import struct
import numpy as np


# reads/writes data from/to a socket
# Authors: Jana Starcukova, jana@isibrno.cz
# Date:    2020



# reads n bytes from socket sc and stores them in the buffer recv_buf
def recv_bytes( sc, toread):
    try:
        # The bytearray() method returns a bytearray object which is an array 
        # of the given bytes.
        recv_buf = bytearray(toread)
        # returns a memory view object of the given argument
        view = memoryview(recv_buf)
       
        while toread>0:
            nbytes = sc.recv_into(view,toread)
            if nbytes == 0:
                break # the connection is closed   
            #_endif
            view = view[nbytes:] 
            toread-=nbytes
        #_endwhile
    except Exception as err:   ##    except ConnectionResetError: in python 3...
        print('Exception caught: %s\nClosing...' % err)
         # ends connection with the client, waits for a need connection (client = sock.accept()[0] )    
    return recv_buf
    #_enddef   


# returns array  of fmt format 
def recv_array( sc, ftm, nItems):
       # get all bytes for nItems of fmt format from socket sc
        recv_buf=recv_bytes(sc, nItems*struct.calcsize(">"+ftm))
        
        # unpack bytes to tuple of nItmes of the type ftm

        tuple = struct.unpack_from(">%d"%nItems+ftm, recv_buf)  
        # convert tuple to array      
        arr = np.asarray(tuple)
        return arr
     

# returns string 
def read_string ( sc):
        strlen = read_int(sc)
        recv_buf=recv_bytes(sc,strlen)
        stringdata = recv_buf.decode('utf-8')
        return stringdata
      
# returns integer    
def read_int( sc):        
        return recv_array(sc,'i',1)[0]

# returns integer
def read_float( sc):
        return recv_array(sc,'f',1)[0]
    
# returns array of doubles
def read_1DArrayDouble( sc):
        nItems = read_int(sc) 
        return  recv_array(sc,'d',nItems)
    
def read_2DArrayDouble( sc, ftm):                     
        size= recv_array(sc,'i',2)
        return recv_array(sc,'d',size[0]*size[1]).reshape(size[0],size[1]) 
    
# writes double array to socket
# sc.. socket
# arr ... numpy array
def write_1DArrayDouble( sc, arr):
        #  sends a bytes object containing doubles packed (big-endian).
        sc.sendall(struct.pack('>%d'%arr.shape[0]+'d',*(arr[:])))


def write_int(sc, intg):
    #  sends a bytes object containing doubles packed (big-endian).
    sc.send(struct.pack(">i", intg))



