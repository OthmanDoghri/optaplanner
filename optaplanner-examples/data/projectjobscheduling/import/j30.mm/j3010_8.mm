************************************************************************
file with basedata            : mf10_.bas
initial value random generator: 686576084
************************************************************************
projects                      :  1
jobs (incl. supersource/sink ):  32
horizon                       :  243
RESOURCES
  - renewable                 :  2   R
  - nonrenewable              :  2   N
  - doubly constrained        :  0   D
************************************************************************
PROJECT INFORMATION:
pronr.  #jobs rel.date duedate tardcost  MPM-Time
    1     30      0       31        4       31
************************************************************************
PRECEDENCE RELATIONS:
jobnr.    #modes  #successors   successors
   1        1          3           2   3   4
   2        3          3           8  13  16
   3        3          3           7  11  24
   4        3          3           5   6  10
   5        3          2           9  11
   6        3          3          11  15  28
   7        3          2          12  18
   8        3          1          12
   9        3          3          23  26  27
  10        3          2          14  20
  11        3          2          14  17
  12        3          2          21  29
  13        3          1          18
  14        3          2          25  29
  15        3          1          24
  16        3          3          19  27  28
  17        3          2          22  26
  18        3          2          19  22
  19        3          1          21
  20        3          3          22  24  30
  21        3          2          26  30
  22        3          3          23  25  27
  23        3          1          29
  24        3          1          25
  25        3          1          31
  26        3          1          31
  27        3          1          31
  28        3          1          30
  29        3          1          32
  30        3          1          32
  31        3          1          32
  32        1          0        
************************************************************************
REQUESTS/DURATIONS:
jobnr. mode duration  R 1  R 2  N 1  N 2
------------------------------------------------------------------------
  1      1     0       0    0    0    0
  2      1     2       0    6    8    0
         2     3       0    5    0    2
         3     9       6    0    7    0
  3      1     3       0    6    4    0
         2     3      10    0    0    8
         3     7       9    0    0    1
  4      1     5       0    9    0    6
         2     6       0    7    5    0
         3     9       1    0    5    0
  5      1     5       0   10    0    8
         2     5       5    0    0    9
         3     7       2    0    0    8
  6      1     1       9    0    0    4
         2     7       0    8    2    0
         3    10       0    6    0    1
  7      1     4       0    6    8    0
         2     4       7    0    3    0
         3     5       0    6    0    6
  8      1     4       7    0    3    0
         2     7       0    6    0    4
         3    10       0    4    0    1
  9      1     4       0    5    0    9
         2     6       0    5    0    3
         3     7       5    0    9    0
 10      1     7       6    0    0    6
         2     9       0    4    4    0
         3     9       3    0    0    6
 11      1     1       6    0    0    6
         2     8       4    0    0    6
         3     9       0    7    0    6
 12      1     1       6    0    6    0
         2     9       0    6    0    9
         3    10       0    4    0    5
 13      1     5      10    0    6    0
         2     6       0    8    5    0
         3     9       0    8    0    5
 14      1     7       0    6    0    7
         2     7       7    0    0    6
         3    10       2    0    5    0
 15      1     4       7    0    0   10
         2     6       0    9   10    0
         3     9       7    0   10    0
 16      1     5       0    2    0    8
         2     6       6    0    0    4
         3    10       0    1    0    3
 17      1     3       9    0    0    5
         2     3       0    2    5    0
         3     7       9    0    5    0
 18      1     4       0    5    7    0
         2     7       4    0    0    5
         3     9       1    0    0    4
 19      1     5       0   10    0    6
         2     8       0   10    6    0
         3     9       0   10    0    5
 20      1     3       0    6    7    0
         2     6       7    0    6    0
         3     7       5    0    3    0
 21      1     6       0    8    8    0
         2     7       8    0    0    7
         3     8       0    7    8    0
 22      1     2       0    7    0    7
         2     7       6    0    3    0
         3     9       0    7    0    4
 23      1     3       2    0    0    3
         2     5       1    0    3    0
         3     6       0    3    0    3
 24      1     5       9    0    8    0
         2     7       8    0    0    9
         3     8       8    0    4    0
 25      1     5       3    0    6    0
         2     5       0    9    6    0
         3     5       3    0    0    8
 26      1     5       9    0    9    0
         2     7       0    7    4    0
         3     9       0    6    0    2
 27      1     1       0    4    3    0
         2     1       2    0    3    0
         3     3       0    4    2    0
 28      1     8       7    0    7    0
         2     9       6    0    0    6
         3    10       6    0    0    5
 29      1     8       6    0    0    5
         2    10       4    0    0    5
         3    10       0    4    0    4
 30      1     1       0    8    9    0
         2     3       0    5    4    0
         3     8       2    0    0   10
 31      1     4       0    8    4    0
         2     5       3    0    4    0
         3     5       0    5    0    9
 32      1     0       0    0    0    0
************************************************************************
RESOURCEAVAILABILITIES:
  R 1  R 2  N 1  N 2
   26   27   80  101
************************************************************************
