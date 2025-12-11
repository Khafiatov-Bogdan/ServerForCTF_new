// cat > rop.c << 'EOF'
#include <stdio.h>
#include <string.h>

void dummy() {
    // Гаджеты для ROP
    asm("pop %rdi; ret");
    asm("ret");
}

int main() {
    char buffer[100];
    
    printf("Введите ROP-цепочку: ");
    gets(buffer);
    
    return 0;
}
// EOF
// gcc rop.c -fno-stack-protector -no-pie -o rop
