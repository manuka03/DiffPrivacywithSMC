import shared3p;
import stdlib;
import shared3p_random;
import shared3p_sort;
domain pd_shared3p shared3p;

uint n = 400;
uint L = 200;
float32 E = 10;
float32 a = 4; float32 b = 10;

float32 [[1]] lvalues(n);

// replace with e differentially private query
bool[[1]] Q(pd_shared3p int64[[2]] T,pd_shared3p bool[[1]] m)
{
    uint r = 1;
    uint n = size(T[:, 0]);
    printVector(declassify(T[:,1]));
    printVector(declassify(m));
    bool[[1]] result(n);
    for(uint i=0; i<n;i++)
    {
        if(declassify(m[i]))
        result[i] = declassify(T[i, r]>17);
    }
    return result;
}

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
pd_shared3p float32 correlation(pd_shared3p int64[[1]] X,pd_shared3p bool[[1]]m)
{
    uint n = size(X);
    pd_shared3p float32[[1]] x(n/2); pd_shared3p float32[[1]] y(n/2);
    pd_shared3p float32 r,s,t,a,b,c,d;
        r = sum((float32)m[:n/2]);
        s = sum((float32)m[:n/2]*(float32)X[:n/2])/(float32)r;
        t = sum((float32)m[:n/2]*(float32)X[n/2:])/(float32)r;
        //print("s and t", declassify(s)); print(declassify(t));
        x = ((float32)X[:n/2]-s)*((float32)m[:n/2]);
        y = ((float32)X[n/2:]-t)*((float32)m[:n/2]);
        a = sum(x*y);
        //print(declassify(a));
        b = sum(x*x);
        d = sum(y*y);
        c = a/sqrt(b*d);

    return c;
}

pd_shared3p float32[[1]] SampleAndAggregate(pd_shared3p int64 [[1]]T, pd_shared3p bool[[1]]m){
    int64 n = (int64)size(T);
    initiate_lvalues(L, (float32)(b-a)/E);
    uint ss = ((uint)n)/L;
    print(ss);
    pd_shared3p int64 [[2]] subsets(L, ss);
    subsets = reshape(T, L, ss);
    pd_shared3p float32 [[1]] Output(L);
    print("correlation is:");
    for(uint i =0;i<L;i++)
    {
        Output[i] = correlation(T[i*ss:i*ss+ss],m[i*ss/2:i*ss+ss/2]);
        //print(declassify(Output[i]));
        Output[i] =(float32)Output[i]+lvalues[i];
        Output[i] = max({Output[i], a});
        Output[i] = min({Output[i], b});
    }
    return Output;
}

float32[[1]] differentiallyPrivateQuery(pd_shared3p int64[[2]] T, pd_shared3p bool[[1]] m, float32 eps, pd_shared3p float32[[1]] B)
{
    uint n = size(m);
    pd_shared3p bool[[1]] a(n);
    a[:n/2] = (((B[:n/2]>eps)&(m[:n/2]))&(B[n/2:]>eps));
    B = B - (float32)a*eps;
    //returnQ(T,a);
    return declassify(SampleAndAggregate(T[:,1], a));
}

void main(){
// below is the initialisation used while testing
    n = n*2;
    pd_shared3p int64[[2]] T(n,2);
    T[:,0] = randomize(T[:,0]);
    T[:,1] = (int64)(((((uint)T[:,0])%1000)+1000)%1000);
    pd_shared3p float32[[1]] B(n);
    B = 100;
    pd_shared3p bool[[1]] m(n);
    m = randomize(m);
//  bool[[1]] adults = differentiallyPrivateQuery(T, m, (float32)50, B);
//  printVector(adults);
    float32[[1]] result = differentiallyPrivateQuery(T, m, E, B);
    //print(result);
}
