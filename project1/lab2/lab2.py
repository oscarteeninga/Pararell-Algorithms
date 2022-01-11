from mpi4py import MPI
import numpy as np
import time
import sys
from matplotlib import pyplot as plt


def down(x, y, begin, down_slice, matrix):
    new_down = down_slice[x]
    if y - 1 == 0:
        new_down = 0
    elif y - 1 >= begin:
        new_down = matrix[y - 1 - begin][x]

    return new_down


def up(x, y, begin, end, size, up_slice, matrix):
    new_up = up_slice[x]
    if y + 1 == size + 1:
        new_up = 0
    elif y + 1 <= end:
        new_up = matrix[y + 1 - begin][x]

    return new_up


def update_matrix(comm, rank, cores, size, matrix, cpy_matrix, up_slice, down_slice, begin, end):
    if rank > 0:
        comm.Send([matrix[0], size + 2, MPI.DOUBLE], dest=rank - 1)
        comm.Recv(down_slice, source=rank - 1)
    if rank < cores - 1:
        comm.Recv(up_slice, source=rank + 1)
        comm.Send([matrix[end - begin], size + 2, MPI.DOUBLE], dest=rank + 1)

    for y in range(begin, end + 1):
        for x in range(1, size + 1):
            new_down = down(x, y, begin, down_slice, matrix)
            new_up = up(x, y, begin, end, size, up_slice, matrix)
            cpy_matrix[y - begin][x] = (matrix[y - begin][x - 1] + matrix[y - begin][x + 1] + new_up + new_down) / 4
    matrix = np.copy(cpy_matrix)
    return matrix


def join(comm, rank, cores, size, matrix, slice_size, result):
    if rank != 0:
        comm.Send([matrix, slice_size * (size + 2), MPI.DOUBLE], dest=0)
    else:
        index = 0

        for row in matrix:
            result[index] = row
            index += 1

        for i in range(cores - 1):
            tmp = np.zeros((slice_size, size + 2), dtype=np.float64)
            comm.Recv(tmp, source=i + 1)

            for v in tmp:
                result[index] = v
                index += 1


def fill_matrix(show, rank, cores, end, begin, slice_size, size, matrix):
    if show and (rank == int(cores / 2) - 1 or rank == int(cores / 2)):
        for y in range(end - begin + 1):
            for j in range(int(size / 2) - slice_size, int(size / 2) + slice_size):
                matrix[y][j] = 200.0


def test():
    # MPI
    comm = MPI.COMM_WORLD
    rank = comm.Get_rank()
    cores = comm.Get_size()

    # Const & Variables
    size = 56  # should be divided by processor count
    iterations = 100
    tests = 1
    show = 0
    slice_size = int(size / cores)
    times = 0

    # Matrix
    cpy_matrix = np.zeros((slice_size, size + 2), dtype=np.float64)
    up_slice = np.zeros(size + 2, dtype=np.float64)
    down_slice = np.zeros(size + 2, dtype=np.float64)
    result = np.zeros((size, size + 2), dtype=np.float64)

    for _ in range(tests):

        begin, end = rank*slice_size+1, (rank+1)*slice_size

        matrix = np.zeros((slice_size, size + 2), dtype=np.float64)
        fill_matrix(show, rank, cores, end, begin, slice_size, size, matrix)

        start = time.time()

        for i in range(iterations):
            matrix = update_matrix(comm, rank, cores, size, matrix, cpy_matrix, up_slice, down_slice, begin, end)
            fill_matrix(show, rank, cores, end, begin, slice_size, size, matrix)

        join(comm, rank, cores, size, matrix, slice_size, result)

        if rank == 0:
            times += time.time() - start

    if rank == 0:
        print(size, cores, times / tests)
        if show:
            plt.imshow(result)
            plt.show()
