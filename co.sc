import shared3p;
import shared3p_random;
import stdlib;
domain pd_shared3p shared3p;

uint n = 10000;
uint L = 100;
float32 a = 0; float32 b = 1;
float32 E = 1;
float32 [[1]] lvalues(L*2);

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
        r = sum((float32)m[:]);
        s = sum((float32)m[:]*(float32)X[:n/2])/(float32)r;
        t = sum((float32)m[:]*(float32)X[n/2:])/(float32)r;
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

float32[[1]] SampleAndAggregate(pd_shared3p int64 [[1]]T, pd_shared3p bool[[1]]m){
    //initiate_lvalues(L, (float32)(b-a)/E);
    uint ss = ((uint)n)/L;
    ss = ss/2;
    print(ss);
    pd_shared3p float32 [[1]] Output(L);
    print("correlation is:");
    for(uint i =0;i<L;i++)
    {
        Output[i] = correlation(cat(T[i*ss:i*ss+ss],T[(n+i*ss):(n+i*ss+ss)]),m[i*ss:i*ss+ss]);
        //print(declassify(Output[i]));
//        Output[i] =(float32)Output[i]+lvalues[i];
//        Output[i] = max({Output[i], a});
//        Output[i] = min({Output[i], b});
    }
    return declassify(Output);
}

void main(){
    pd_shared3p int64 [[1]] X(n);
    pd_shared3p int64 [[1]] Y(n);
    pd_shared3p int64 [[1]] T(n);
    X = randomize(X); Y = randomize(Y);
    T = cat(X,Y);
    T = (int64)(((((uint)T)%1000)+1000)%1000);
    pd_shared3p bool [[1]] m(n);
    bool tr = true;
    m = tr;
    //n = n*2;
    float32[[1]] result = SampleAndAggregate(T, m);
}
