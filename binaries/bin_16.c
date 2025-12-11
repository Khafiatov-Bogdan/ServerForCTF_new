// cat > shellcode.c << 'EOF'
#include <stdio.h>
#include <string.h>

int main() {
    char buffer[100];
    
    printf("Введите shellcode: ");
    gets(buffer); // Нет защиты NX!
    
    // Исполнение shellcode
    void (*func)() = (void (*)())buffer;
    func();
    
    return 0;
}
// EOF
// gcc shellcode.c -fno-stack-protector -z execstack -no-pie -o shellcode
