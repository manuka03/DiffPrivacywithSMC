import shared3p;
import stdlib;
import shared3p_random;
import shared3p_sort;
domain pd_shared3p shared3p;

uint n = 1000;
uint L = 1000;
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
    pd_shared3p uint8[[1]] rd_values(L);
    pd_shared3p bool[[1]] sign(L);
    sign = randomize(sign);
    rd_values = (randomize(rd_values)%100+100)%100;
    lvalues[:L] = ln(1-(float32)declassify(rd_values)/(float32)100);
    lvalues[:L] = (declassify(sign))?lvalues[:L]:(-1.0)*lvalues[:L];
}

pd_shared3p float32 f(pd_shared3p int64[[1]] arr, pd_shared3p bool[[1]] m)
{
    pd_shared3p float32 avg = 0;
    uint n = size(arr);
    arr = arr*(int)m;
    avg = (float32)(sum(arr))/(float32)sum(m);
    return avg;
}


float32 SampleAndAggregate(pd_shared3p int64 [[1]]T, pd_shared3p bool[[1]]m){
    int64 n = (int64)size(T);
    initiate_lvalues(L, (float32)(b-a)/E);
    uint subset_size = (uint)n/L;
    print(subset_size);
    pd_shared3p float32 [[1]] Output(L);
    for(uint i =0;i<L;i++)
    {
        Output[i] = f(T[i*subset_size:i*subset_size+subset_size], m[i*subset_size:i*subset_size+subset_size]);
        Output[i] = max({Output[i], a});
        Output[i] = min({Output[i], b});
        Output[i] =(float32)Output[i]+lvalues[i];
        //print("Output ",i, " is ", Output[i]);
    }
    float32 aggregate =  declassify((float32)sum(Output))/(float32)L;
    return 0;
}


float32 differentiallyPrivateQuery(pd_shared3p int64[[2]] T, pd_shared3p bool[[1]] m, float32 eps, pd_shared3p float32[[1]] B)
{
    uint n = size(m);
    pd_shared3p bool[[1]] a(n);
    a = ((B>eps)&(m));
    B = B - (float32)a*eps;
    //returnQ(T,a);
    return SampleAndAggregate(T[:,1], a);
}

void main(){
// general way of initialisation
//    pd_shared3p int64[[2]] T(4,2);
//    T[0,:] = {1, 16};
//    T[1,:] = {2, 34};
//    T[2,:] = {3, 20};
//    T[3,:] = {3, 5};
//    pd_shared3p float32[[1]] B(4) = {220, 200, 130, 140};
//    pd_shared3p bool[[1]] m(4) = {true,true,true,true};

// below is the initialisation used while testing
    pd_shared3p int64[[2]] T(n,2);
    T[:,0] = 4;
    T[:,1] = 6;// all values initialised to the same value to check output
    pd_shared3p float32[[1]] B(n);
    B = 100;
    pd_shared3p bool[[1]] m(n);
    m = randomize(m);
    float32 result = differentiallyPrivateQuery(T, m, E, B);
    print(result);
}
