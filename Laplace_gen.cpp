#include <stdlib.h>
#include <math.h>
#include <fstream>
#include <iostream>
#include <random>

using namespace std;

using namespace std;
default_random_engine generator;
double laplace(double s) {
  uniform_real_distribution<double> rng(0.0, 1.0);
  double x = log(1- rng(generator))*s;
  if(rng(generator)<0.5)
      x = -x;
  return x;
}

int main() {
  int n = 10;
  double a,b,e; b = 10; a =0; e = 4;
  ofstream outfile("output.csv");
  if (!outfile.is_open()) {
    cerr << "Error opening output file." << endl;
    return 1;
  }
  outfile << "Laplace" << endl;
  for (int i = 0; i < n; ++i) {
    outfile << laplace((b-a)/e) << endl;
  }

  outfile.close();

  cout << "CSV file created successfully." << endl;

  return 0;
}
