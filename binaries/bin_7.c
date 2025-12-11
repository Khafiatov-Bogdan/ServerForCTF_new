// cat > md5_check.c << 'EOF'
#include <stdio.h>
#include <string.h>
#include <openssl/md5.h>

int main() {
    char input[] = "CTF{md5_input_string}";
    unsigned char digest[MD5_DIGEST_LENGTH];
    
    MD5((unsigned char*)input, strlen(input), digest);
    
    printf("MD5 хеш строки вычислен\n");
    return 0;
}
// EOF
// if command -v gcc &> /dev/null; then
    // gcc md5_check.c -o md5_check -no-pie -lcrypto 2>/dev/null || \
    // gcc md5_check.c -o md5_check -no-pie -DNO_SSL
//     echo "Бинарник создан: binaries/md5_check"
// else
//     echo "gcc не найден, пропускаем..."
// fi
