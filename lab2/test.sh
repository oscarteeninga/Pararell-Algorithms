for size in 56 112 168 224 280; do
	for p in {1..8}; do
		mpiexec -np $p python3 cpu.py $size 100 10 0
	done
	printf "\n"
done
