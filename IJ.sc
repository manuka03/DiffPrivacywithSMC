import shared3p;
import stdlib;
import shared3p_random;
import shared3p_sort;
domain pd_shared3p shared3p;
uint Vr = 1000;
uint Br = 1000;
void printMatrix(pd_shared3p int64[[2]] R)
{
    public uint Rrows = size(R[:,0]);
    public uint Rcols = size(R[0,:]);
    print("Rrows is", Rrows);
    print("Rcols is", Rcols);
    for(uint i = 0; i<Rrows; i++)
    {
//         for(uint j = 0; j<Rcols; j++)
//         {
//            print(declassify(R[i,j]));
//         }
        printVector(declassify(R[i,:]));
    }
}
pd_shared3p int [[2]] propogateBack(pd_shared3p int[[2]] M, pd_shared3p int[[1]] p){
    print("size of M ", size(M[:,0]), " Size of p ", size(p[:]));
    uint j = 1;
    uint n = size(p);
    uint col = size(M[0,:]);

    pd_shared3p int[[2]] X(n,col);
    while(j<n){
        for(uint k =0;k<col;k++)
        {X[:n-j, k] = (int)(p[:n-j]==p[j:]);}
        M[:n-j,:] = X[:n-j,:]*(M[j:,:])+(1-X[:n-j,:])*(M[:n-j,:]);
        j*=2;
    }
    return M;
}

pd_shared3p int [[2]] joiningSecretSharedTables(pd_shared3p int[[2]] V, pd_shared3p int[[2]] B, uint s, uint t)
{
    public uint Vr = size(V[:,0]);
    public uint Br = size(B[:,0]);
    public uint Vc = size(V[0,:]);
    public uint Bc = size(B[0,:]);
    print(Vr," ", Br, " ", Vc, " ", Bc);
    pd_shared3p int[[2]] A(Vr + Br,2+Vc+Bc);

    int[[2]] Zero_blbc(Vr,Bc) = 0; int[[2]] Zero_blvc(Br,Vc) = 0;
    int[[2]] Zero_vr(Vr,1) = 0; int[[2]] One_br(Br,1) = 1;
    A[:Vr, :] = cat(cat(cat(V[:,s:s+1], Zero_vr,1), V[:,:], 1), Zero_blbc,1);
    A[Vr:, :] = cat(cat(cat(B[:,t:t+1], One_br,1), Zero_blvc, 1),B[:,:],1);

    A = sortingNetworkSort(A, (uint64)0, (uint64)1);
    A[:, Vc+2:] = propogateBack(A[:,Vc+2:], A[:,0]);
    //printMatrix(A);
    pd_shared3p int64 [[2]] R(Vr,Vc+Bc);
    uint it = 0;
    for(uint i = 0; i<Vr+Br; i++)
    {
        public bool b = declassify(A[i,1]==0);
        if(b)
        {
            R[it,:] = A[i, 2:];
            it +=1;
        }
    }
    //printMatrix(R);
    return R;
}

void main(){
//    pd_shared3p int64[[2]] V(2,2);
//    V[0,:] = {1,340};
//    V[1,:] = {2, 440};
//    pd_shared3p int64[[2]] B(2,2);
//    B[0,:] = {1, 34};
//    B[1,:] = {2, 45};
    pd_shared3p int64[[2]] V(Vr,2);
    pd_shared3p int64[[2]] B(Br,2);
    randomize(V); randomize(B);
    pd_shared3p int64[[2]] R = joiningSecretSharedTables(V, B, (uint)0, (uint)0);
    //printMatrix(R);
}
