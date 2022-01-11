export TAU_HOME=/home/path/to/tau-2.28.2/build/x86_64
export TAU_MAKEFILE=/home/path/to/tau-2.28.2/build/x86_64/lib/Makefile.tau-papi-ompt-tr4-mpi-pdt-openmp
export PATH=$TAU_HOME/bin:$PATH

export TAU_COMM_MATRIX=1
export TAU_THROTTLE=1
export OMP_NUM_THREADS=4
export TRACK_MEMORY_FOOTPRINT=1
export TAU_SAMPLING=1
export TAU_VERBOSE=1
export TAU_OMPT_SUPPORT_LEVEL=full
export TAU_OMPT_RESOLVE_ADDRESS_EAGERLY=1

mpiexec -np 8 python3 lab2.py 80 100 10 0