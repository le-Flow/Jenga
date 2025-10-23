import { createContext, Resource, createResource, JSXElement, createSignal, createEffect, Accessor, createMemo } from "solid-js"
import { AuthenticationResourceService, LoginRequestDTO, LoginResponseDTO, OpenAPI, RegisterRequestDTO, User } from "../api"

type UserContextType = {
    user?: User
    login?: (request: LoginRequestDTO) => void;
    isLoggedIn: Accessor<boolean>
    register?: (request: RegisterRequestDTO) => void;

    jwt: Resource<LoginResponseDTO>
}


export const UserContext = createContext<UserContextType>()

interface ProviderProps {
    children: JSXElement
}

export const UserProvider = (props: ProviderProps) => {

    const register = (request: RegisterRequestDTO) => {
        setRegisterRequestDTO(request)
    }

    const login = (request: LoginRequestDTO) => {
        setLoginRequestDTO(request)
    }

    const [registerRequestDTO, setRegisterRequestDTO] = createSignal<RegisterRequestDTO>()
    const [loginRequestDTO, setLoginRequestDTO] = createSignal<LoginRequestDTO>()

    const [registerResult] = createResource(registerRequestDTO, async (q: RegisterRequestDTO) => await AuthenticationResourceService.postApiAuthRegister(q))
    const [loginResult] = createResource(loginRequestDTO, async (q: LoginRequestDTO) => {
        const jwt = await AuthenticationResourceService.postApiAuthLogin(q);
        OpenAPI.TOKEN = jwt.token
        OpenAPI.USERNAME = jwt.username

        return jwt
    })

    const loggedIn = createMemo(() => !!(!loginResult.error && loginResult()?.token));

    const value = {
        login: login,
        isLoggedIn: loggedIn,
        register: register,
        jwt: loginResult
    }


    return (
        <UserContext.Provider value={value}>
            {props.children}
        </UserContext.Provider>
    )
}