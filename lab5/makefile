build: clean
	 mpicc -c tsp.c
	 mpicc -o tsp.x tsp.o

run: build
	mpiexec -np 1 ./tsp.x

clean:
	rm -rf *.x *.x *.o