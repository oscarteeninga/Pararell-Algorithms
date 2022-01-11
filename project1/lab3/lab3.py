from mpi4py import MPI
import numpy as np
import time

G = 10

# index
X = 0
Y = 1
Z = 2
M = 3
ID = 4


class Position:
    @classmethod
    def norm(cls, first_pos, second_pos):
        return np.sqrt(
            np.power(first_pos[X] - second_pos[X], 2) +
            np.power(first_pos[Y] - second_pos[Y], 2) +
            np.power(first_pos[Z] - second_pos[Z], 2)
        )


class Star:
    @classmethod
    def size(cls):
        return 1 + 1 + 3

    @classmethod
    def compute_a(cls, star, constellation):
        a = [0, 0, 0]
        for other_star in constellation:
            if star[ID] != other_star[ID]:
                norm = np.power(Position.norm(star, other_star), 3)
                a[X] += star[M] * (star[X] - other_star[X]) / norm
                a[Y] += star[M] * (star[Y] - other_star[Y]) / norm
                a[Z] += star[M] * (star[Z] - other_star[Z]) / norm

        return [a[X]*G, a[Y]*G, a[Z]*G]


class Constellation:
    @classmethod
    def random(cls, size):
        constellation = np.zeros((size, Star.size()), dtype=np.float64)
        for id in range(size):
            constellation[id][ID] = id
            constellation[id][X] = np.random.uniform(0, 10)
            constellation[id][Y] = np.random.uniform(0, 10)
            constellation[id][Z] = np.random.uniform(0, 10)
            constellation[id][M] = np.random.uniform(10, 20)

        return constellation

    @classmethod
    def sequence(cls, constellation):
        return [Star.compute_a(star, constellation) for star in constellation]

    @classmethod
    def check(cls, size, expected, result):
        for idx in range(size):
            for dim in range(3):
                if abs(result[idx][dim] - expected[idx][dim]) > 1e-5:
                    print("NOT EQUAL!")
                    return
        print("EQUAL")

    @classmethod
    def parallel(cls, size):

        comm = MPI.COMM_WORLD
        rank = comm.Get_rank()
        cores = comm.Get_size()

        if size < 2 or cores < 2:
            start = time.time()
            Constellation.sequence(Constellation.random(size))
            print(str(size) + "\t1\t" + str(time.time() - start))
            return

        slice_size = int(size / cores)

        if rank:
            constellation_slice = np.zeros((slice_size, Star.size()), dtype=np.float64)
            comm.Recv(constellation_slice, source=0)
        else:
            constellation = Constellation.random(size)
            constellation_slice = constellation[0:slice_size]
            for c in range(1, cores):
                comm.Send([constellation[c*slice_size:(c+1)*slice_size], slice_size * Star.size(), MPI.DOUBLE], dest=c)

        neighbour_slice = np.zeros((slice_size, Star.size()), dtype=np.float64)
        acc = np.zeros((slice_size, 3), dtype=np.float64)

        to_send = constellation_slice

        start = time.time()

        for _ in range(cores):
            comm.Send([to_send, slice_size * Star.size(), MPI.DOUBLE], dest=(rank + 1) % cores)
            comm.Recv(neighbour_slice, source=(rank - 1) % cores)
            for star in constellation_slice:
                a = Star.compute_a(star, neighbour_slice)
                idx = int(star[ID] % slice_size)
                acc[idx][X] += a[X]
                acc[idx][Y] += a[Y]
                acc[idx][Z] += a[Z]

            to_send = neighbour_slice

        if rank:
            comm.Send(acc, dest=0)
        else:
            result = np.zeros((size, 3), dtype=np.float64)
            result[0:slice_size] = acc
            for c in range(1, cores):
                comm.Recv(result[c*slice_size:(c+1)*slice_size], source=c)
            #
            # print(str(size) + "\t" + str(cores) + "\t" + str(time.time() - start))
            # Constellation.check(size, result, Constellation.sequence(constellation))
