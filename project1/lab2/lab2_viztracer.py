from viztracer import VizTracer
from lab2 import test
from mpi4py import MPI
tracer = VizTracer()
tracer.start()

# Execute program
test()
tracer.stop()
tracer.save('lab2_result' + str(MPI.COMM_WORLD.Get_rank()) + '.json')