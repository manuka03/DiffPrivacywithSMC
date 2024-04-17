import shared3p;
import shared3p_random;
import stdlib;
domain pd_shared3p shared3p;
uint n = 100;
uint L = 100;

float32 [[1]] lvalues(n);

void initiate_lvalues(uint L, float32 s){
    pd_shared3p uint16[[1]] rd_values(L);
    bool[[1]] sign(L);
    rd_values = (randomize(rd_values)%1000+1000)%1000;
    //printVector(declassify(rd_values));
    lvalues[:L] = declassify((float32)rd_values/(float32)1000) - 0.5;
    sign = lvalues[:L]>0;
    lvalues[:L] = ln(1.0001-2*abs(lvalues[:L]));
    lvalues[:L] = (sign)?lvalues[:L]*s:lvalues[:L]*(-1)*s;
    lvalues[:L] = lvalues[:L];
    //printVector(lvalues);
}

pd_shared3p float32 f(pd_shared3p int64[[1]] arr)
{
    pd_shared3p float32 avg = 0;
    uint n = size(arr);
    avg = (float32)(sum(arr))/(float32)n;
    return avg;
}

float32 SampleAndAggregate(pd_shared3p int64 [[1]]T, uint L, float32 a, float32 b){
    int64 n = (int64)size(T);
    uint subset_size = (uint)n/L;
    print(subset_size);
    pd_shared3p float32 [[1]] Output(L);
    for(uint i =0;i<L;i++)
    {
        Output[i] = f(T[i*subset_size:i*subset_size+subset_size]);
        Output[i] = max({Output[i], a});
        Output[i] = min({Output[i],b});
        Output[i] =(float32)Output[i]+lvalues[i];
        //print("Output ",i, " is ", Output[i]);
    }
    float32 aggregate =  declassify((float32)sum(Output))/(float32)L;
    return aggregate;
}

void main(){
    float32 a = 0; float32 b = 10;
    float32 E = 1;
    initiate_lvalues(L, (b-a)/E);
    pd_shared3p int64 [[1]] T(n) = 6;
    float32 result = SampleAndAggregate(T, L, a, b);
    print("final result is ", result);
}
