
import { ActionBar, SubmitBar } from "@egovernments/digit-ui-react-components";
import {  Link } from "react-router-dom";
import React from "react";


const NoSurveyFoundPage = ({t}) => {
    return (<React.Fragment>
        <div style={{textAlign : "-webkit-center", marginTop:"30%", marginRight:"10%"}}>
        <svg width="255" height="245" viewBox="0 0 255 245" fill="none" xmlns="http://www.w3.org/2000/svg">
            <g clip-path="url(#clip0_47664_105169)">
            <path d="M214.607 58.0692H213.267V21.2879C213.267 15.642 211.027 10.2273 207.039 6.23508C203.051 2.24282 197.641 2.77689e-09 192.001 2.77689e-09H114.153C111.36 -4.51493e-05 108.595 0.550538 106.015 1.62031C103.435 2.69009 101.09 4.2581 99.1157 6.23483C97.1409 8.21157 95.5744 10.5583 94.5056 13.141C93.4369 15.7238 92.8868 18.492 92.8867 21.2875V223.073C92.8867 228.719 95.1273 234.133 99.1154 238.125C103.104 242.118 108.513 244.36 114.153 244.36H192C197.64 244.36 203.049 242.118 207.038 238.125C211.026 234.133 213.266 228.719 213.266 223.073V84.2507H214.606L214.607 58.0692Z" fill="#3F3D56"/>
            <path d="M208.905 19.8617V224.497C208.904 228.778 207.206 232.883 204.182 235.91C201.159 238.938 197.058 240.639 192.782 240.641H113.337C109.059 240.641 104.956 238.941 101.93 235.914C98.9044 232.886 97.2039 228.78 97.2026 224.497V19.8617C97.2039 15.5795 98.9043 11.4732 101.93 8.44588C104.955 5.41857 109.058 3.7182 113.336 3.71875H122.973C122.5 4.88362 122.32 6.14702 122.449 7.39789C122.577 8.64876 123.011 9.84881 123.712 10.8926C124.412 11.9363 125.358 12.7918 126.467 13.3838C127.575 13.9759 128.812 14.2863 130.068 14.2879H175.361C176.617 14.2863 177.854 13.9759 178.962 13.3838C180.07 12.7918 181.016 11.9363 181.717 10.8926C182.418 9.84881 182.851 8.64876 182.98 7.39789C183.109 6.14702 182.929 4.88362 182.456 3.71875H192.779C197.055 3.71948 201.156 5.42005 204.18 8.44661C207.204 11.4732 208.904 15.578 208.905 19.8586V19.8617Z" fill="white"/>
            <path d="M153.04 102.01C171.357 102.01 186.207 87.1455 186.207 68.8088C186.207 50.4722 171.357 35.6074 153.04 35.6074C134.722 35.6074 119.872 50.4722 119.872 68.8088C119.872 87.1455 134.722 102.01 153.04 102.01Z" fill="#a82227"/>
            <path d="M189.213 137.735H118.616C118.27 137.734 117.937 137.596 117.692 137.351C117.447 137.106 117.309 136.773 117.309 136.426V118.896C117.31 118.55 117.447 118.217 117.692 117.972C117.938 117.726 118.27 117.588 118.616 117.588H189.213C189.559 117.588 189.891 117.726 190.137 117.972C190.382 118.217 190.519 118.55 190.52 118.896V136.426C190.519 136.773 190.382 137.106 190.137 137.351C189.891 137.596 189.559 137.735 189.213 137.735V137.735ZM118.616 118.111C118.408 118.111 118.209 118.194 118.062 118.341C117.915 118.489 117.832 118.688 117.832 118.896V136.426C117.832 136.635 117.915 136.834 118.062 136.981C118.209 137.129 118.408 137.211 118.616 137.212H189.213C189.421 137.211 189.62 137.129 189.767 136.981C189.914 136.834 189.997 136.635 189.997 136.426V118.896C189.997 118.688 189.914 118.489 189.767 118.341C189.62 118.194 189.421 118.111 189.213 118.111H118.616Z" fill="#E6E6E6"/>
            <path d="M140.449 123.901C140.206 123.901 139.973 123.998 139.802 124.17C139.63 124.341 139.534 124.575 139.534 124.817C139.534 125.06 139.63 125.293 139.802 125.465C139.973 125.637 140.206 125.734 140.449 125.734H183.591C183.833 125.743 184.07 125.655 184.248 125.49C184.426 125.325 184.531 125.096 184.541 124.853C184.55 124.61 184.462 124.374 184.297 124.195C184.132 124.017 183.903 123.912 183.661 123.902C183.647 123.902 183.634 123.902 183.621 123.902L140.449 123.901Z" fill="#E6E6E6"/>
            <path d="M140.449 129.396C140.206 129.396 139.973 129.493 139.802 129.665C139.63 129.837 139.534 130.07 139.534 130.313C139.534 130.556 139.63 130.789 139.802 130.96C139.973 131.132 140.206 131.229 140.449 131.229H183.591C183.833 131.238 184.07 131.15 184.248 130.985C184.426 130.82 184.531 130.591 184.541 130.348C184.55 130.105 184.462 129.869 184.297 129.69C184.132 129.512 183.903 129.407 183.661 129.397C183.647 129.397 183.634 129.397 183.621 129.397L140.449 129.396Z" fill="#E6E6E6"/>
            <path d="M189.213 166.953H118.616C118.27 166.953 117.937 166.815 117.692 166.57C117.447 166.324 117.309 165.992 117.309 165.645V148.115C117.31 147.768 117.447 147.436 117.692 147.19C117.938 146.945 118.27 146.807 118.616 146.807H189.213C189.559 146.807 189.891 146.945 190.137 147.19C190.382 147.436 190.519 147.768 190.52 148.115V165.645C190.519 165.992 190.381 166.324 190.136 166.57C189.891 166.815 189.559 166.953 189.213 166.953ZM118.616 147.33C118.408 147.33 118.209 147.413 118.062 147.56C117.915 147.707 117.832 147.907 117.832 148.115V165.645C117.832 165.853 117.915 166.053 118.062 166.2C118.209 166.347 118.408 166.43 118.616 166.43H189.213C189.421 166.43 189.62 166.347 189.767 166.2C189.914 166.053 189.997 165.853 189.997 165.645V148.115C189.997 147.907 189.914 147.707 189.767 147.56C189.62 147.413 189.421 147.33 189.213 147.33H118.616Z" fill="#E6E6E6"/>
            <path d="M140.449 153.214C140.206 153.214 139.973 153.31 139.802 153.482C139.63 153.654 139.534 153.887 139.534 154.13C139.534 154.373 139.63 154.606 139.802 154.778C139.973 154.95 140.206 155.046 140.449 155.046H183.591C183.83 155.045 184.059 154.95 184.229 154.781C184.399 154.613 184.497 154.384 184.501 154.145C184.505 153.905 184.415 153.674 184.25 153.5C184.086 153.326 183.86 153.223 183.621 153.214H140.449V153.214Z" fill="#E6E6E6"/>
            <path d="M140.449 158.711C140.206 158.711 139.973 158.807 139.802 158.979C139.63 159.151 139.534 159.384 139.534 159.627C139.534 159.87 139.63 160.103 139.802 160.275C139.973 160.447 140.206 160.543 140.449 160.543H183.591C183.83 160.542 184.059 160.447 184.229 160.278C184.399 160.11 184.497 159.881 184.501 159.642C184.505 159.402 184.415 159.171 184.25 158.997C184.086 158.823 183.86 158.72 183.621 158.711H140.449V158.711Z" fill="#E6E6E6"/>
            <path d="M189.213 196.267H118.616C118.27 196.266 117.937 196.129 117.692 195.883C117.447 195.638 117.309 195.305 117.309 194.958V177.429C117.31 177.082 117.447 176.749 117.692 176.504C117.938 176.259 118.27 176.121 118.616 176.12H189.213C189.559 176.121 189.891 176.259 190.137 176.504C190.382 176.749 190.519 177.082 190.52 177.429V194.959C190.519 195.306 190.382 195.638 190.137 195.883C189.891 196.129 189.559 196.267 189.213 196.267V196.267ZM118.616 176.643C118.408 176.644 118.209 176.726 118.062 176.874C117.915 177.021 117.832 177.221 117.832 177.429V194.959C117.832 195.167 117.915 195.366 118.062 195.514C118.209 195.661 118.408 195.744 118.616 195.744H189.213C189.421 195.744 189.62 195.661 189.767 195.514C189.914 195.366 189.997 195.167 189.997 194.959V177.429C189.997 177.221 189.914 177.021 189.767 176.874C189.62 176.726 189.421 176.644 189.213 176.643H118.616Z" fill="#E6E6E6"/>
            <path d="M140.449 182.529C140.206 182.529 139.973 182.626 139.802 182.798C139.63 182.969 139.534 183.202 139.534 183.445C139.534 183.688 139.63 183.921 139.802 184.093C139.973 184.265 140.206 184.362 140.449 184.362H183.591C183.711 184.366 183.831 184.347 183.943 184.305C184.056 184.264 184.16 184.2 184.248 184.118C184.336 184.036 184.407 183.938 184.457 183.829C184.508 183.719 184.536 183.601 184.541 183.481C184.545 183.361 184.526 183.241 184.484 183.128C184.443 183.015 184.379 182.912 184.297 182.823C184.216 182.735 184.117 182.664 184.008 182.613C183.899 182.563 183.781 182.535 183.661 182.53C183.647 182.53 183.634 182.53 183.621 182.53L140.449 182.529Z" fill="#E6E6E6"/>
            <path d="M140.449 188.025C140.206 188.025 139.973 188.122 139.802 188.294C139.63 188.466 139.534 188.699 139.534 188.942C139.534 189.184 139.63 189.418 139.802 189.589C139.973 189.761 140.206 189.858 140.449 189.858H183.591C183.711 189.862 183.831 189.843 183.943 189.801C184.056 189.76 184.16 189.696 184.248 189.614C184.336 189.533 184.407 189.434 184.457 189.325C184.508 189.216 184.536 189.097 184.541 188.977C184.545 188.857 184.526 188.737 184.484 188.624C184.443 188.511 184.379 188.408 184.297 188.319C184.216 188.231 184.117 188.16 184.008 188.11C183.899 188.059 183.781 188.031 183.661 188.026C183.647 188.026 183.634 188.026 183.621 188.026H140.449L140.449 188.025Z" fill="#E6E6E6"/>
            <path d="M254.334 245H0.665646C0.297787 245 0 244.839 0 244.641C0 244.442 0.29813 244.281 0.665646 244.281H254.334C254.702 244.281 255 244.442 255 244.641C255 244.839 254.702 245 254.334 245Z" fill="#E6E6E6"/>
            <path d="M243.591 230.984C242.507 231.365 241.344 231.461 240.212 231.261C239.081 231.062 238.02 230.574 237.132 229.845C234.869 227.944 234.16 224.812 233.583 221.913L231.876 213.336L235.45 215.799C238.02 217.571 240.648 219.399 242.428 221.965C244.208 224.531 244.984 228.034 243.554 230.81" fill="#E6E6E6"/>
            <path d="M243.039 241.965C243.489 238.684 243.952 235.361 243.636 232.046C243.356 229.101 242.458 226.225 240.631 223.866C239.661 222.617 238.481 221.547 237.144 220.703C236.795 220.483 236.474 221.036 236.821 221.255C239.135 222.719 240.925 224.881 241.932 227.429C243.045 230.263 243.224 233.351 243.032 236.361C242.915 238.181 242.67 239.989 242.422 241.795C242.403 241.877 242.415 241.963 242.456 242.036C242.498 242.109 242.565 242.163 242.646 242.188C242.727 242.21 242.814 242.199 242.888 242.157C242.961 242.115 243.016 242.046 243.038 241.965H243.039Z" fill="#F2F2F2"/>
            <path d="M239.801 236.7C239.337 237.408 238.698 237.983 237.946 238.371C237.195 238.759 236.356 238.947 235.511 238.917C233.339 238.814 231.529 237.297 229.899 235.857L225.078 231.595L228.269 231.443C230.563 231.333 232.917 231.23 235.104 231.938C237.291 232.647 239.305 234.351 239.704 236.614" fill="#E6E6E6"/>
            <path d="M244.311 243.85C242.147 240.015 239.633 235.752 235.145 234.389C233.897 234.011 232.589 233.876 231.29 233.991C230.88 234.025 230.983 234.658 231.393 234.622C233.57 234.441 235.742 235.017 237.545 236.252C239.28 237.434 240.631 239.077 241.774 240.82C242.474 241.885 243.101 243 243.728 244.111C243.928 244.466 244.514 244.21 244.311 243.85Z" fill="#F2F2F2"/>
            <path d="M127.423 133.05C130.456 133.05 132.914 130.59 132.914 127.554C132.914 124.518 130.456 122.058 127.423 122.058C124.391 122.058 121.933 124.518 121.933 127.554C121.933 130.59 124.391 133.05 127.423 133.05Z" fill="#a82227"/>
            <path d="M124.795 126.527C124.731 126.527 124.68 126.938 124.68 127.443C124.68 127.949 124.731 128.36 124.795 128.36H130.194C130.257 128.368 130.309 127.965 130.31 127.459C130.345 127.144 130.307 126.825 130.197 126.528H124.795V126.527Z" fill="#E6E6E6"/>
            <path d="M127.423 162.376C130.456 162.376 132.914 159.915 132.914 156.879C132.914 153.844 130.456 151.383 127.423 151.383C124.391 151.383 121.933 153.844 121.933 156.879C121.933 159.915 124.391 162.376 127.423 162.376Z" fill="#a82227"/>
            <path d="M124.795 155.853C124.731 155.853 124.68 156.263 124.68 156.768C124.68 157.274 124.731 157.685 124.795 157.685H130.194C130.257 157.693 130.309 157.29 130.31 156.784C130.345 156.469 130.307 156.15 130.197 155.853L124.795 155.853Z" fill="#E6E6E6"/>
            <path d="M127.423 191.69C130.456 191.69 132.914 189.229 132.914 186.194C132.914 183.158 130.456 180.697 127.423 180.697C124.391 180.697 121.933 183.158 121.933 186.194C121.933 189.229 124.391 191.69 127.423 191.69Z" fill="#a82227"/>
            <path d="M124.795 185.167C124.731 185.167 124.68 185.577 124.68 186.083C124.68 186.589 124.731 186.999 124.795 186.999H130.194C130.257 187.007 130.309 186.604 130.31 186.098C130.345 185.784 130.307 185.465 130.197 185.167L124.795 185.167Z" fill="#E6E6E6"/>
            <path d="M145.875 59.6918L143.932 61.6367L160.204 77.9258L162.147 75.9808L145.875 59.6918Z" fill="white"/>
            <path d="M162.147 61.6373L160.204 59.6924L143.932 75.9815L145.875 77.9264L162.147 61.6373Z" fill="white"/>
            <path d="M71.002 156.83C71.478 156.662 71.9113 156.391 72.2713 156.036C72.6314 155.682 72.9094 155.253 73.0858 154.779C73.2622 154.305 73.3328 153.799 73.2925 153.295C73.2522 152.791 73.102 152.302 72.8526 151.863L80.9466 142.626L74.6282 141.754L67.9107 150.659C67.201 151.127 66.6875 151.84 66.4677 152.662C66.2479 153.484 66.3369 154.358 66.718 155.119C67.099 155.88 67.7456 156.474 68.5352 156.789C69.3248 157.105 70.2025 157.119 71.002 156.83Z" fill="#9F616A"/>
            <path d="M80.7632 238.688L75.1182 238.688L72.4326 216.892L80.7642 216.892L80.7632 238.688Z" fill="#9F616A"/>
            <path d="M82.2023 244.166L64 244.165V243.935C64.0001 242.054 64.7466 240.25 66.0752 238.92C67.4039 237.59 69.2058 236.843 71.0848 236.843H71.0853L82.2026 236.843L82.2023 244.166Z" fill="#2F2E41"/>
            <path d="M110.313 233.959L104.922 235.634L95.9004 215.615L103.857 213.143L110.313 233.959Z" fill="#9F616A"/>
            <path d="M113.311 238.763L95.9269 244.166L95.8586 243.945C95.3015 242.149 95.4801 240.205 96.3549 238.54C97.2298 236.876 98.7294 235.627 100.524 235.069L100.524 235.069L111.142 231.77L113.311 238.763Z" fill="#2F2E41"/>
            <path d="M76.4396 138.198L68.0791 147.783L74.6244 148.373L76.4396 138.198Z" fill="#a82227"/>
            <path d="M73.173 150.552C73.173 150.552 70.269 152.006 69.9059 158.546C69.5429 165.087 70.9951 182.167 70.9951 182.167C70.9951 182.167 69.5432 189.798 70.9951 196.702C72.4469 203.606 69.5432 228.678 71.3581 228.678C73.173 228.678 82.611 229.768 82.974 228.678C83.3371 227.587 83.7001 211.236 83.7001 211.236C83.7001 211.236 86.6042 202.878 83.7001 197.792C83.7001 197.792 93.8397 215.516 100.398 229.042C101.833 232.002 112.74 228.679 110.925 225.408C109.11 222.138 104.754 207.24 104.754 207.24C104.754 207.24 101.487 196.338 95.6794 191.614L98.5835 168.359C98.5835 168.359 104.755 152.733 101.125 150.553C97.4957 148.373 73.173 150.552 73.173 150.552Z" fill="#2F2E41"/>
            <path d="M84.0629 91.322C89.2754 91.322 93.5009 87.0921 93.5009 81.8744C93.5009 76.6566 89.2754 72.4268 84.0629 72.4268C78.8505 72.4268 74.625 76.6566 74.625 81.8744C74.625 87.0921 78.8505 91.322 84.0629 91.322Z" fill="#A0616A"/>
            <path d="M94.5899 92.0488L82.8568 98.9528C82.8568 98.9528 78.3642 101.485 76.686 105.857C74.8972 110.516 75.9369 117.075 76.686 118.575C78.1379 121.482 76.077 128.749 76.077 128.749L74.2621 145.464C74.2621 145.464 67.7282 151.642 72.8103 152.368C77.8923 153.095 86.9672 152.005 92.4123 152.368C97.8573 152.732 104.028 153.459 102.213 149.824C100.398 146.189 98.2204 143.647 100.398 136.381C102.103 130.694 102.029 109.431 101.916 100.451C101.901 99.2177 101.57 98.0091 100.956 96.9398C100.342 95.8706 99.4655 94.9763 98.4089 94.3421L94.5899 92.0488Z" fill="#a82227"/>
            <path opacity="0.1" d="M89.3278 103.496L90.4166 125.662L79.8631 147.056L78.0732 146.374L88.9634 126.388L89.3278 103.496Z" fill="black"/>
            <path opacity="0.1" d="M101.305 130.385V127.841L88.9648 148.19L101.305 130.385Z" fill="black"/>
            <path d="M77.3647 73.1222L75.8057 72.4978C75.8057 72.4978 79.0687 68.9025 83.607 69.215L82.3303 67.8081C82.3303 67.8081 85.4508 66.5576 88.2879 69.8401C89.7792 71.5657 91.5045 73.5943 92.5813 75.879H94.2523L93.555 77.4162L95.9957 78.9535L93.4904 78.6784C93.7277 80.0055 93.6465 81.3701 93.2534 82.6596L93.3221 83.8746C93.3221 83.8746 90.4167 79.3743 90.4167 78.7499V80.3133C90.4167 80.3133 88.8563 78.9064 88.8563 77.9684L88.0052 79.0628L87.5796 77.3437L82.3314 79.0628L83.1818 77.6552L79.9188 78.1242L81.1955 76.405C81.1955 76.405 77.5076 78.4371 77.3658 80.1569C77.2239 81.8767 75.3802 84.0648 75.3802 84.0648L74.529 82.5014C74.529 82.5014 73.2527 75.4671 77.3647 73.1222Z" fill="#2F2E41"/>
            <path d="M83.4712 157.858C83.9097 157.608 84.2881 157.264 84.5795 156.851C84.871 156.439 85.0685 155.967 85.1582 155.47C85.2478 154.972 85.2274 154.461 85.0984 153.972C84.9694 153.484 84.735 153.029 84.4116 152.641L90.7392 142.113L84.3665 142.377L79.3346 152.334C78.7192 152.921 78.3404 153.714 78.2698 154.562C78.1992 155.41 78.4419 156.255 78.9518 156.936C79.4617 157.617 80.2033 158.087 81.0363 158.257C81.8693 158.427 82.7356 158.285 83.4712 157.858Z" fill="#9F616A"/>
            <path d="M97.8577 96.7735L100.58 96.2285C100.58 96.2285 108.748 102.224 106.206 114.215C103.665 126.207 92.4126 144.375 92.4126 144.375C92.4126 144.375 89.8709 147.646 89.1455 148.372C88.4201 149.099 86.9676 148.372 87.6937 149.463C88.4198 150.553 86.6045 151.279 86.6045 151.279C86.6045 151.279 78.6184 151.279 79.3445 148.372C80.0706 145.465 92.4126 124.752 92.4126 124.752L90.5977 105.493C90.5977 105.493 89.1452 96.0466 97.8577 96.7735Z" fill="#a82227"/>
            </g>
            <defs>
            </defs>
        </svg>
        <h style={{color:"#505A5F", fontWeight:"400", fontFamily:"Roboto", marginLeft:"10%", lineHeight:"3"}}>{t("SURVEY_ENDED_MESSAGE")}</h>
        </div>
        <ActionBar>
        <Link to={"/upyog-ui/citizen"}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
        </ActionBar>
    </React.Fragment>)
}

export default NoSurveyFoundPage;