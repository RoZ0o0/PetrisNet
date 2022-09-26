module.exports = {
    root: true,
    env: {
        node: true
    },
    extends: [
        'plugin:vue/vue3-essential',
        '@vue/standard',
        '@vue/typescript/recommended'
    ],
    parserOptions: {
        ecmaVersion: 2020
    },
    rules: {
        'no-console': 'warn',
        'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
        'no-trailing-spaces': 'error',
        'no-unused-vars': 'off',
        'no-use-before-define': 'off',
        semi: ['error', 'always'],
        'space-before-function-paren': ['error', {
            anonymous: 'never',
            named: 'never',
            asyncArrow: 'always'
        }],
        'keyword-spacing': 'error',
        '@typescript-eslint/no-unused-vars': 'off',
        'vue/html-indent': ['error', 2, {
            alignAttributesVertically: false
        }]
    }
};