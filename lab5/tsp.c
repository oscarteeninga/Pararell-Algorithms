/*
 The parallel Branch and bound is implemented as follows
  Nodes are assigned such that each processor gets one node each
  If size/nprocs is > 1, then each processor is assigned size/nprocs number of nodes
  Each node is taken as the current starting node
  DFS is done on this node to find its nearest neighbor based on the cost metric.
 Further searching of the current branch are pruned by using the cost of the best
 solution so far.
  If the cost of traveling to this city is less than the global best, the new node is taken
 as the current node and DFS is performed on this node
  This process goes on till all the nodes have been visited and we visit the root node
 back
 
 */

#include <float.h>
#include <getopt.h>
#include <limits.h>
#include <math.h>
#include <mpi.h>
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

typedef struct
{
  int line, v;
  float x, y;
} City;

typedef struct
{
  int pointA, pointB;
  float cost;
} Way;

int n, lrank, nprocs;
char locsf[255];
double gen_time, proc_time, comm_time, total_time;

City *locs;

void swap(int *p1, int *p2);
float calcost(int *a);
void display(int *a, int *c, float cost);
float distance(City a, City b);
int nearest(int curr, int start, int end);
void n_neigh();
int parse_args(int argc, char **argv);
void generate_random();

int main(int argc, char **argv)
{
  printf("Dupa");
  n = 1;
  generate_random();

  MPI_Init(&argc, &argv);
  MPI_Comm_rank(MPI_COMM_WORLD, &lrank);
  MPI_Comm_size(MPI_COMM_WORLD, &nprocs);

  double t_start = MPI_Wtime();

  n_neigh();

  if (lrank == 0)
    printf("%d\tt\t%d\t%f\n", n, nprocs, MPI_Wtime() - t_start);


  free(locs);
  MPI_Finalize();
  return 0;
}

void swap(int *p1, int *p2)
{
  int temp;
  temp = *p1;
  *p1 = *p2;
  *p2 = temp;
}

float calcost(int *a)
{
  float cost = 0.0f;
  for (int i = 0; i < n - 1; i++)
    cost += distance(locs[a[i] - 1], locs[a[i + 1] - 1]);
  return cost;
}

void display(int *a, int *c, float cost)
{
  int x;
  for (x = 0; x < n; x++)
    printf("%d  ", a[x]);
  printf("cost:%f c:%d\n", cost, *c);
}

float distance(City a, City b)
{
  return sqrt(pow(a.x - b.x, 2) + pow(a.y - b.y, 2));
}

int nearest(int curr, int start, int end)
{
  int i, index = -1;
  float min = FLT_MAX;
  float dist;

  for (i = start; i <= end; ++i)
  {
    dist = distance(locs[curr], locs[i]);
    if (dist < min && i != curr && locs[i].v == 0)
    {
      min = dist;
      index = i;
    }
  }
  return index;
}

void n_neigh()
{
  int i, j, index, sloc, eloc, next;
  int locpn = n / nprocs;
  int *inm;
  int fpath[n];
  double start, end, dt;
  float min = FLT_MAX;
  float dist;
  float cost = 0.0f;
  inm = (int *)malloc(sizeof(int) * nprocs);
  sloc = locpn * lrank;
  eloc = sloc + locpn - 1;
  if (lrank == nprocs - 1)
    eloc += n % nprocs;
  next = 0;
  fpath[0] = 0;
  for (i = 0; i < n - 1; i++)
  {
    start = MPI_Wtime();
    MPI_Bcast(&next, 1, MPI_INT, 0, MPI_COMM_WORLD);
    end = MPI_Wtime();
    dt = end - start;
    comm_time += dt;
    start = MPI_Wtime();
    locs[next].v = 1;
    int index = nearest(next, sloc, eloc);
    end = MPI_Wtime();
    dt = end - start;
    proc_time += dt;
    start = MPI_Wtime();
    MPI_Gather(&index, 1, MPI_INT, inm, 1, MPI_INT, 0, MPI_COMM_WORLD);
    end = MPI_Wtime();
    dt = end - start;
    comm_time += dt;
    if (lrank == 0)
    {
      start = MPI_Wtime();
      index = inm[0];
      min = FLT_MAX;
      for (j = 0; j < nprocs; ++j)
      {
        if (inm[j] < 0)
          continue;
        dist = distance(locs[next], locs[inm[j]]);
        if (dist < min)
        {
          min = dist;
          index = inm[j];
        }
      }
      next = index;
      fpath[i + 1] = index;
      end = MPI_Wtime();
      dt = end - start;
      proc_time += dt;
    }
    MPI_Barrier(MPI_COMM_WORLD);
  }
  if (lrank == 0)
  {
    for (i = 0; i < n; ++i)
      printf("%d ", fpath[i]);
    printf("\n");
  }
  free(inm);
}

void generate_random(int n)
{
  locs = (City *)malloc(sizeof(City) * n);

  for (int i = 0; i < n; i++)
  {
    locs[i].line = i + 1;
    locs[i].x = (float)(rand() % 1000);
    locs[i].y = (float)(rand() % 1000);
    locs[i].v = 0;
  }
}
