// cat > len_check.c << 'EOF'
#include <stdio.h>
#include <string.h>

int main() {
    char correct[] = "CTF{len_eq_8}";
    char input[20];
    
    printf("Введите пароль (8 символов): ");
    fgets(input, sizeof(input), stdin);
    input[strcspn(input, "\n")] = 0;
    
    if (strlen(input) == 8) {
        printf("Длина верная!\n");
        if (strcmp(input, correct) == 0) {
            printf("Флаг: %s\n", correct);
        }
    } else {
        printf("Неверная длина!\n");
    }
    
    return 0;
}
// EOF
// gcc len_check.c -o len_check -no-pie
