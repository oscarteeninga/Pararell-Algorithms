# does not work for sizes above 200, idk why
for size in 56 104 152 200; do
	for p in {1..8}; do
		mpiexec -np $p python3 pararell.py $size
	done
done