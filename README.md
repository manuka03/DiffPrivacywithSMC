# Combining Differential Privacy with Secure Multiparty Computations 
This is an implementation of the algorithms described in the paper https://dl.acm.org/doi/10.1145/2818000.2818027.
There are three code files each for Average calculation and Correlation calculation written in sharemind's SecreC language. 
The code files implement 3 different levels of differential private query's on any dataset. 
1. Global Budget Differentially Privacy 
2. Personalised Differential Privacy 
3. Personalised Differential Privacy
</br>
The paper is focussed on the algorithms that can be used for introducing differential privacy to existing SMC protocols as well as analysing their computational overhead. Sharemind's SDK is used as a base for SMC protocols.

<h2>Requirements to run:</h2>
<p>
    I have used a virtual environment (VirtualBox) to run the Sharemind SDK, which supports the SecreC language. <br>
    The VM image with inbuilt SecreC 2 compiler can be downloaded from <a href="https://sharemind.cyber.ee/sharemind-mpc/">https://sharemind.cyber.ee/sharemind-mpc/</a>. <br>
    Once downloaded in the system with SecreC 2 compiler, the code files can be opened using the pre-installed Qt Creator. <br>
    The parameters are hardcoded at the top like 'n' and 'L'; they can be changed accordingly, and the code can be run using the keyboard shortcut Ctrl+Fn+F2.
</p>
<h2>Code files:</h2>
<p>
    There are 3 Code files each for Average and Correlation calculation. The following is a brief explanation of the files. <br><br>
    <b>1. <i>SA_avg.sc/SA_co.sc :</i></b> It implements the sample and aggregate mechanism. It includes a subroutine which computes f without privacy considerations. This function is called as a black box L times for L disjoint subsets of original dataset T of approximately equal sizes.To ensure that the added noise aligns with the differential privacy definition, it's essential to clip the output values accordingly before introducing the noise This is an implementation of the global budget version of differential privacy. The values of 'E', 'L', 'n' can be changed as per requirement. </br> <br>
    <b>2. <i>PDP_avg.sc/PDP_co.sc :</i></b> It implements the Personalised Differential Privacy without provenances. This checks whether the current privacy budget allows for the query to be executed and accordingly calculated the new individual budgets and updated mask vector before calling the sample and aggregate function. These individual privacy budget updates are independent, thus can be executed parallely, reducing the overhead. <br><br>
    <b>3. <i>PDP_withProvenances_avg.sc/PDP_withProvenances_co.sc :</i></b> It implements the Personalised Differential Privacy with provenances. The provenance budgets are assumed to be stored in a different table with some value for all provenances. The frequency of values from each provenances is calculated differentially privately, and accordingly the mask vectors and budgets are updated. The algorithm given runs in O(nlogn) time where n is the sum of number of rows in the dataset and provenances tables. </br>
</p>

## Code Change for Evaluation : 
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
## Results :
The paper is aimed at determining the time complexity of adding differential privacy to secure multiparty computations. So to evaluate my implementation of the paper, I have used plotted the estimated running times of my simulations. These running times are shown in the runtime terminals as shown. 
</br>
<img src="https://github.com/manuka03/DiffPrivacywithSMC/assets/99576067/ce66ac04-228c-45cf-86c2-46813ef66223" alt="image" width="700" height="600"> </br>
The non differentially private time taken on my system is approximated by commenting out the lower and upper limit comparisions and laplace noise formation and addition. The global budget version corresponds to SA.sc described earlier. 
The following graphs represent the time complexities of the algorithms implemented. <br> 
For Correlation Calculation: <br>
![image](https://github.com/manuka03/DiffPrivacywithSMC/assets/99576067/9826958d-0014-461d-936c-ddcfa26c5ab0)

For Average Calculation: <br>
![image](https://github.com/manuka03/DiffPrivacywithSMC/assets/99576067/0e60b742-c684-4f68-b099-b7b88b9a1d46)
<br>
## Conclusion:
It is possible to combine differential privacy with Secure multiparty computations and create a doubly secure framework which protects the interests of the individuals as well as the mass data providers. The computational overhead of such a framework is not prohibitive. 
