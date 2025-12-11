// cat > env.c << 'EOF'
#include <stdio.h>
#include <stdlib.h>

int main() {
    char* flag_var = getenv("FLAG_VAR");
    
    if (flag_var != NULL) {
        printf("Флаг: %s\n", flag_var);
    } else {
        printf("Установите переменную FLAG_VAR\n");
    }
    
    return 0;
}
// EOF
// gcc env.c -o env_program -no-pie
