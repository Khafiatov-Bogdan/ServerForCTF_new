// cat > canary_leak.c << 'EOF'
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main() {
    unsigned int canary = 0x00ffd2a3;
    char input[100];
    
    printf("Введите форматную строку: ");
    fgets(input, sizeof(input), stdin);
    
    printf(input); // Утечка canary
    
    printf("\nCanary должен быть: 0x%08x\n", canary);
    return 0;
}
// EOF
// gcc canary_leak.c -o canary_leak -no-pie
