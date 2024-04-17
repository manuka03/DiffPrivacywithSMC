# Combining Differential Privacy with Secure Multiparty Computations 
This is an implementation of the paper https://dl.acm.org/doi/10.1145/2818000.2818027.
There are three code files each for Average calculation and Correlation calculation written in sharemind's SecreC language. 
The three code files implement 4 different levels of differential private query's on any dataset. 
1. Non-Differentially Private (approximated by commenting out the laplace value addition and comparisions from SA.sc)
2. Global Budget Differentially Privacy (Implemented by Sample and aggregate mechanism)
3. Personalised Differential Privacy (without Provenances)
4. Personalised Differential Privacy (with Provenances)

Additionally, the .ipynb notebook has the relevant graphs as asked during code evaluation. 
Initially, I have checked the validity of the laplace value generation function by comparing its output with the library function in python. 
Having validated that, I have adopted the sample and aggregate function to python and presented histograms for 1000 iterations the sample and aggregate function the output over various values of E.
