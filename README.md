## Combining Differential Privacy with Secure Multiparty Computations 
This is an implementation of the algorithms described in the paper https://dl.acm.org/doi/10.1145/2818000.2818027.
There are three code files each for Average calculation and Correlation calculation written in sharemind's SecreC language. 
The code files implement 3 different levels of differential private query's on any dataset. 
1. Global Budget Differentially Privacy 
2. Personalised Differential Privacy 
3. Personalised Differential Privacy
The paper is focussed on the algorithms that can be used for introducing differential privacy to existing SMC protocols as well as analysing their computational overhead. Sharemind's SDK is used as a base for SMC protocols.
<br>
### Requirements to run:
I have used a virtual environment (VirtualBox) to run the Sharemind sdk which supports the SecreC language. <br>
The VM image with inbuilt SecreC 2 compiler can be downloaded from https://sharemind.cyber.ee/sharemind-mpc/ <br>
Once downloaded in the system with secreC 2 compiler, the code files can be opened using the pre-installed Qt creator. <br>
The parameters are hardcoded at the top like n and L they can be changed accordingly and the code can be run using the keyboard shortcut ctrl+fn+F2 <br> 
### Code files: 
There are 3 Code files each for Average and Correlation calculation. The following is a brief explanation of the files.
1. <b>SA_avg.sc/SA_co.sc :</b> It implementes of the sample and aggregate mechanism. This is an implementation of the global budget version of differential privacy.The values of E, L, n can be changed as per requirement.
2. <b>PDP_avg.sc/PDP_co.sc :</b> It implements of the Personalised Differential Privacy without provenances.  
3. <b> PDP_withProvenances_avg.sc/PDP_withProvenances_co.sc : </b> It implements the Personalised Differential Privacy with provenances. The provenance budgets are assument to be stored in a different table with some value for all provenances.
<br>
### Code Change for Evaluation : 
The .ipynb notebook has the relevant graphs as asked during code evaluation. 
Initially, I have checked the validity of the laplace value generation function by comparing its output with the library function in python. The graphs are also shown below for reference
<br>
<img src="https://github.com/manuka03/DiffPrivacywithSMC/assets/99576067/c913b0aa-1310-4342-97b8-4121c6bbbd7b" alt="image" width="500" height="300">
<img src="https://github.com/manuka03/DiffPrivacywithSMC/assets/99576067/340f991f-e210-4e56-a55a-63b72a9f9202" alt="image" width="500" height="300">
<br>
Having validated that, I have adapted the Sample and aggregate function (from SA.sc) to python.<br>
The following code blocks in the jupyter notebook calculate the average over a dataset initialised to 6 (arbitrarily chosen). This calculation is done 1000 times and the outputs are plotted into a histogram. <br>
For the histograms, I have used a common scale for the first histogram (Blue), so that the effect of E can be observed with ease. For some of the histograms, I have added a rescaled version of the same histogram so that the distribution is apparent.
As we can see, as E is reduced increasing the privacy protection, the output is more varied as compared to a larger value of E. However, in both cases the output is centered around the mean value. 
<br>
### Results :
The following graphs represent the time complexities of the algorithms implemented. 
For Correlation Calculation:<br>
<img src="https://github.com/manuka03/DiffPrivacywithSMC/assets/99576067/07028790-6022-4dcb-bb43-0351a58249b0" alt="image" width="500" height="300">
For Average Calculation: <br>
<img src="https://github.com/manuka03/DiffPrivacywithSMC/assets/99576067/d9cfd728-011c-45db-ba3c-8604ab60c0e9" alt="image" width="500" height="300">
### Conclusion:
It is possible to combine differential privacy with Secure multiparty computations and create a doubly secure framework which protects the interests of the individuals as well as the mass data providers. The computational overhead of such a framework is not prohibitive. 
