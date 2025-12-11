// cat > fmt.c << 'EOF'
#include <stdio.h>

int main() {
    char flag[] = "CTF{stack_leak_success}";
    char input[100];
    
    printf("Введите текст: ");
    fgets(input, sizeof(input), stdin);
    
    printf(input); // Уязвимость форматной строки!
    
    return 0;
}
// EOF
// gcc fmt.c -o fmt -no-pie
