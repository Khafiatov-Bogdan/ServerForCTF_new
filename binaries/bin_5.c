// cat > check.c << 'EOF'
#include <stdio.h>
#include <string.h>

int main() {
    char secret[] = "admin";
    char input[20];
    
    printf("Введите логин: ");
    fgets(input, sizeof(input), stdin);
    input[strcspn(input, "\n")] = 0;
    
    if (strcmp(input, secret) == 0) {
        printf("Флаг: CTF{admin_access_granted}\n");
    } else {
        printf("Доступ запрещен\n");
    }
    
    return 0;
}
// EOF
// gcc check.c -o check -no-pie
