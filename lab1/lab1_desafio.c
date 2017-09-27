//
//  lab1_desafio.c
//  
//
//  Created by Erick Kelvin on 13/09/17.
//
//

#include "lab1_desafio.h"

double fib(int n) {
    return ((1/sqrt(5)) * pow((((1 + sqrt(5)))/2), n)) - ((1/sqrt(5)) * pow((((1 - sqrt(5)))/2), n));
}

int main(int argc, char *argv[])  {
    if( argc == 3 ) {
        int range_min = atoi(argv[1]) - 1; //decrementa pois começa do 0, não do 1
        int range_max = atoi(argv[2]) - 1; //decrementa pois começa do 0, não do 1
        
        
        int fda[2];
        double envio;
        double retorno;
        
        if (pipe(fda) == -1) {
            printf("Erro ao criar o pipe");
        }
        
        
        switch (fork()) {
            case -1:
                printf("Erro no fork");
                break;
            case 0:
                close(fda[0]);
                for (int i=range_min; i<=range_max; i++) {
                    //printf("%.0f ", fib(i));
                    envio = fib(i);
                    write (fda[1], &envio, (range_max-range_min)*sizeof(double));
                }
                break;
            default:
                close(fda[1]);
                for (int i=range_min; i<=range_max; i++) {
                    read (fda[0], &retorno, (range_max-range_min)*sizeof(double));
                    printf("%.0f ", retorno);
                }
                break;
        }
        
    }
    else {
        printf("A entrada deve ser no formato 'lab1_desafio range_min range_max'\n");
    }
    
    return (0);
}
