
// cat > login.c << 'EOF'
#include <stdio.h>
#include <string.h>

int main() {
    char correct_password[] = "CTF{super_secret_pwd}";
    char input[50];
    
    printf("Введите пароль: ");
    fgets(input, sizeof(input), stdin);
    input[strcspn(input, "\n")] = 0;
    
    if (strcmp(input, correct_password) == 0) {
        printf("Доступ разрешен!\n");
    } else {
        printf("Неверный пароль!\n");
    }
    
    return 0;
}
// EOF
// gcc login.c -o login -no-pie
