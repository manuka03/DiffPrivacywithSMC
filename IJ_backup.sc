import shared3p;
import stdlib;
import shared3p_random;
import shared3p_sort;
domain pd_shared3p shared3p;
uint Vr = 10;
uint Br = 50;
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
    uint j = 1;
    uint n = size(p);
    while(j<n){
        for(uint i=0; i<n-j; i++){
         bool b = declassify(p[i]==p[i+j]);
         if(b)
         {M[i,:] += (M[i+j,:]-M[i,:]);}
        }
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

    int [[1]] Zero_vc(Vc) =0; int [[1]] Zero_bc(Bc) = 0;
    int[[2]] Zero_blbc(Vr,Bc) = 0;
    int[[2]] Zero_vr(Vr,1) = 0;
//    for(uint i=0; i<Vr; i++)
//    {
//        A[i,:] = cat(cat(cat({V[i, s]}, {0}), V[i,:]), Zero_bc);
//    }
    cat(V[:,s:s], Zero_vr,1);
    //A[:Vr, :] = cat(cat(cat(V[:, s:s], Zero_vr, 0), V, 0), Zero_blbc, 0);
    for(uint i=0; i<Br; i++)
    {
        A[i+Vr,:] = cat(cat(cat({B[i, t]}, {1}), Zero_vc), B[i,:]);
    }
    A = sortingNetworkSort(A, (uint64)0, (uint64)1);
    //printMatrix(A);
    A[:, Vc+2:] = propogateBack(A[:,Vc+2:], A[:,0]);
    //printMatrix(A);
    pd_shared3p int64 [[2]] R(Vr,4);
    uint it = 0;
    for(uint i = 0; i<size(A[:,0]); i++)
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
    //        A[i,:] = cat(cat(cat({V[i, s]}, {0}), V[i,:]), Zero_bc);
    int [[1]] Zero_vc(2) =0; int [[1]] Zero_bc(2) = 0;
    int[[2]] Zero_blbc(Vr,2) = 0;
    int[[2]] Zero_vr(Vr,1) = 0; uint s = 0;
    pd_shared3p int[[2]] X(Vr,2) = cat(V[:,s:s+1], Zero_vr,1);
    cat(cat(cat(V[:,s:s], Zero_vr,1), V[:,:],1), Zero_blbc,1);
    //pd_shared3p int64[[2]] R = joiningSecretSharedTables(V, B, (uint)0, (uint)0);
    //printMatrix(R);
}
