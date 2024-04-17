import shared3p;
import stdlib;
import shared3p_random;
import shared3p_sort;
import IJ;

uint n = 10;
uint L = 20;
float32 E = 10;
float32 a = 4; float32 b = 10;

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
bool[[1]] Q(pd_shared3p int64[[2]] T,pd_shared3p bool[[1]] m)
{
    uint r = 1;
    uint n = size(T[:, 0]);
    bool[[1]] result(n);
    for(uint i=0; i<n;i++)
    {
        if(declassify(m[i]))
        result[i] = declassify(T[i, r]>17);
    }
    return result;
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
        //Output[i] =(float32)Output[i]+lvalues[i];
        //Output[i] = max({Output[i], a});
        //Output[i] = min({Output[i], b});
    }
    return declassify(Output);
}

pd_shared3p int64[[1]] createFrequencyTable(pd_shared3p int64[[1]] v){
    uint n = size(v);
    pd_shared3p int64[[1]] freq(n);
    int64[[1]] J(n);
    for(uint j = 1; j<n; j=j*2){
        J[:] = (int)j;
        freq[j:] = freq[j:]+(int64)(v[j:]==v[:n-j])*(freq[:n-j]+J[:n-j] - freq[j:]);
        //for(uint i = j; i<n; i++){
        //    freq[i] = freq[i]+((int64)(v[i]==v[i-j]))*(freq[i-j]+(int64)j-freq[i]);
        //} These 2 operations are equivalent
    }
    return freq;
}

float32[[1]] differentiallyPrivateQuery(pd_shared3p int64[[2]] V, pd_shared3p bool[[1]] m, int64 eps, pd_shared3p int64[[2]] B){
    public uint Vr = size(V[:,0]);
    public uint Br = size(B[:,0]);
    public uint Vc = size(V[0,:]);
    public uint Bc = size(B[0,:]);
    //print(Vr," ", Br, " ", Vc, " ", Bc);

    pd_shared3p int[[2]] A(Vr + Br,3+Vc+Bc);

    int[[2]] Zero_blbc(Vr,Bc) = 0; int[[2]] Zero_blvc(Br,Vc) = 0;
    int[[2]] Zero_vr(Vr,1) = 0; int[[2]] One_br(Br,1) = 1; int[[2]]Zero_br(Br,1) = 0;
    pd_shared3p bool[[2]] mask = reshape(m,2*n,1);
    uint s = 0; uint t = 0;
    A[:Vr, :] = cat(cat(cat(cat(V[:,s:s+1], Zero_vr,1), V[:,:],1), Zero_blbc,1), (int)mask,1);
    A[Vr:, :] = cat(cat(cat(cat(B[:,t:t+1], One_br,1), Zero_blvc,1),B[:,:],1),  Zero_br, 1);
    //printMatrix(declassify(A));
    A = sortingNetworkSort(A, (uint64)0, (uint64)1);
    pd_shared3p int64[[1]] f = createFrequencyTable(A[:,0]);
    pd_shared3p int64[[2]] h(Vr+Br, 1);
    //printVector(declassify(A[:, Vc+Bc+1]));
    h[:,0] = ((int64)(A[:,Vc+Bc+1]>=f*eps));
    A[:, Vc+Bc+1] = A[:, Vc+Bc+1] - h[:,0]*f*eps;
    h = propogateBack(h, A[:,0]);
    //print("After propogate back");
    //printMatrix(A);
    //printVector(declassify(h[:,0]));
    A[:,Vc+Bc+2] = (int64)(A[:,Vc+Bc+2]+h[:,0]>1);
    A = shuffleRows(A);
    A = sortingNetworkSort(A, (uint)1);
    V = A[:Vr, 2:Vc+2];
    B = A[Vr:, 2+Vc:];
    //printMatrix(A);
    m = (bool)A[:Vr, Vc+Bc+2];
    //print("The new m is");
    //printVector(declassify(m));
    //return Q(V, m);
    return SampleAndAggregate(V[:,1],m);
}

void main(){
//    pd_shared3p int64[[2]] V(4,2);
//    V[0,:] = {1, 16};
//    V[1,:] = {2, 34};
//    V[2,:] = {3, 20};
//    V[3,:] = {3, 5};
//    pd_shared3p int64[[2]] B(3,2);
//    B[0,:] = {1, 130};
//    B[1,:] = {2, 50};
//    B[2,:] = {3, 102};
    // for testing :
    pd_shared3p int64[[2]] V(n*2,2);
    V[:,0] = randomize(V[:,0]);
    V[:,1] = 10;
    V[:,0] = (int64)((uint)V[:,0]%1000);

    pd_shared3p int64[[2]] B(n,2);
    B[:,0] = randomize(B[:,0]);
    B[:,0] = (int64)((uint)B[:,0]%1000);
    B[:,1] = 1000;


    pd_shared3p bool [[1]] m(2*n);
    bool tr = true;
    m = tr;

    float32[[1]] result = differentiallyPrivateQuery(V, m, (int64)E, B);
}
