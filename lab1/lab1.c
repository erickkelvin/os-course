//
//  lab1.c
//
//
//  Created by Erick Kelvin on 13/09/17.
//
//

#include "lab1.h"

double fib(int n) {
    return ((1/sqrt(5)) * pow((((1 + sqrt(5)))/2), n)) - ((1/sqrt(5)) * pow((((1 - sqrt(5)))/2), n));
}

int main(int argc, char *argv[])  {
    if( argc == 3 ) {
        int range_min = atoi(argv[1]) - 1; //decrementa pois começa do 0, não do 1
        int range_max = atoi(argv[2]) - 1; //decrementa pois começa do 0, não do 1
        
        if ((range_min<0)||(range_max<0)) {
            printf("ERRO! Os limites de entrada devem ser positivos!\n");
        }
        else {
            int n_children = range_max - range_min;
            pid_t pid;
            for (int i=range_min; i<=range_max; i++) {
                if ((pid = fork()) < 0) {                   //fail
                    printf("Erro na criação do fork!");
                    abort();
                }
                else if (pid == 0) {                        //child
                    printf("%.0f ", fib(i));
                    exit(0);
                }
                else {                                      //parent
                    wait(NULL);
                }
            }

        }
        
    }
    else {
        printf("ERRO! A entrada deve ser no formato 'lab1 range_min range_max'\n");
    }
    
    return (0);
}

