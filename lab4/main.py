import random
import sys

ID = 0
DEADLINE = 1
TIME = 2


def generate_tasks(count) -> list[list[int]]:
    # Task is [id, deadline, time]
    return [[i, random.randint(1, count), random.randint(1, count)] for i in range(count)]

def maks()
if __name__ == '__main__':
    tasks_count = int(sys.argv[1])
    machines_count = int(sys.argv[2])

