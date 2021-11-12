import math
import matplotlib.pyplot as plt
def First_derivative(D_i,G,b1,b2,b3,D_max,H_max,f1,f2,f3):
    Function=(G*D_i*(1-(D_i*(b1+b2*D_i-b3*D_i*D_i))/(D_max*H_max)))/(274+3*b2*D_i-4*b3*D_i*D_i)*f1*f2*f3
    return Function

#for maple
b1=137
D_max=152.5
H_max=3011
G=170
t2=200
c=1.57


#for beech
##b1=137
##D_max=122
##H_max=2660
##G=150
##t2=300
##c=2.2


#for fir - question about G
##b1=137
##D_max=50
##H_max=1830
##G=200
##t2=80
##c=2.5


#for spruce
##b1=137
##D_max=50
##H_max=1830
##G=100
##t2=350
##c=2.5


###for birch - maybe change some parameters (b1 and 274)
##b1=137
##D_max=46
##H_max=1830
##G=240
##t2=80
##c=0.486


t1=0

h=1

N=int((t2-t1)/h)+1 #calculation of the number of calculation steps for the corrected Euler method
t=[0]*N
D=[0]*N
H=[0]*N
LA=[0]*N
f2=[0]*N

#initial conditions
t[0]=t1
D[0]=5

b2=(2*(H_max-b1))/D_max
b3=(H_max-b1)/(D_max*D_max)

H[0]=(b1+b2*D[0]-b3*D[0]*D[0])
LA[0]=c*D[0]*D[0]/10000

#for water function
if D[0]<5:
    amount_of_water_per_week=1
else:
    if D[0]>=5 and D[0]<30:
        amount_of_water_per_week=2
    else:
        amount_of_water_per_week=10
f2[0]=(LA[0]*32.44)/(amount_of_water_per_week*52*D[0]*39.3701/(264.172*100))


#for light function
f1=1


#for temperature function
a_1=-1/5062500
b_1=11/10125
c_1=-40/81
mean_temp=[1.8, 2.1, 5.3, 9.4, 13.5, 16.8, 18.7, 18.2, 14.6, 10.5, 6, 2.7]
days_in_month=[31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
for i in range (len(mean_temp)):
    mean_temp[i]=(mean_temp[i]-4.4)*days_in_month[i]
DD=sum(mean_temp)
f3=a_1*DD*DD+b_1*DD+c_1
print("f3 = ", f3)

#Calculations for the corrected Euler method
for i in range (1,N):
    t[i]=t[i-1]+h
    D[i]=D[i-1]+h*0.5*(First_derivative(D[i-1],G,b1,b2,b3,D_max,H_max,f1,f2[i-1],f3)+First_derivative(D[i-1]+h*First_derivative(D[i-1],G,b1,b2,b3,D_max,H_max,f1,f2[i-1],f3),G,b1,b2,b3,D_max,H_max,f1,f2[i-1],f3))
    H[i]=(b1+b2*D[i]-b3*D[i]*D[i])
    LA[i]=c*D[i]*D[i]/10000
    #for water function
    if D[i]<5:
        amount_of_water_per_week=1
    else:
        if D[i]>=5 and D[i]<30:
            amount_of_water_per_week=2
        else:
            amount_of_water_per_week=10
    f2[i]=(LA[i]*32.44)/(amount_of_water_per_week*52*(D[i]*39.3701/100)/264.172)

#Output of calculation results in tabular form
print()
print("The results of calculations by the corrected Euler method:")
print() 
print(" № of iter    time, t    diameter,D (cm)    height, H (cm)  leaf area, LA (m^2)  ")
print(" ————————————————————————————————————————————————————")
for i in range (N):
    print("| {:^10d}".format(i),"|{:=6.3f}".format(t[i]),"| {:^14.9f}".format(D[i]),"| {:^14.9f}".format(H[i]),"| {:^14.9f}".format(LA[i]),"|")
    print(" ———————————————————————————————————————————————————— ")
print()
  
fig = plt.figure() #graph of diameter change
plt.plot(t, D, "-")
plt.grid(True)
plt.title("diameter")
plt.ylabel("D")
plt.xlabel("t")
fig = plt.figure() #graph of height change
plt.plot(t, H, "-")
plt.grid(True)
plt.title("height")
plt.ylabel("H")
plt.xlabel("t")
plt.show()
 
