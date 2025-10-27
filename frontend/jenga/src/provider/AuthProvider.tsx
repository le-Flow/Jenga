import { Accessor, JSXElement, Resource, createContext, createMemo, createResource, createSignal } from "solid-js";
import { AuthenticationResourceService, LoginRequestDTO, LoginResponseDTO, OpenAPI, RegisterRequestDTO } from "../api";

type AuthContextType = {
  login?: (request: LoginRequestDTO) => void;
  isLoggedIn: Accessor<boolean>;
  register?: (request: RegisterRequestDTO) => void;
  jwt: Resource<LoginResponseDTO | undefined>;
  registerResult: Resource<LoginResponseDTO | undefined>;
  logout?: () => void;
};

export const AuthContext = createContext<AuthContextType>();

interface ProviderProps {
  children: JSXElement;
}

export const AuthProvider = (props: ProviderProps) => {
  const [registerRequestDTO, setRegisterRequestDTO] = createSignal<RegisterRequestDTO>();
  const [loginRequestDTO, setLoginRequestDTO] = createSignal<LoginRequestDTO>();

  const register = (request: RegisterRequestDTO) => {
    setRegisterRequestDTO(request);
  };

  const login = (request: LoginRequestDTO) => {
    setLoginRequestDTO(request);
  };

  const [registerResult] = createResource(registerRequestDTO, (payload) =>
    payload ? AuthenticationResourceService.postApiAuthRegister(payload) : undefined
  );
  const [loginResult, { mutate: setLoginResult }] = createResource(loginRequestDTO, async (payload) => {
    if (!payload) {
      return undefined;
    }


      return await AuthenticationResourceService.postApiAuthLogin(payload);

  });

  const loggedIn = createMemo(() => {
    if (loginResult.error) {
      OpenAPI.TOKEN = undefined;
      OpenAPI.USERNAME = undefined;
      return false;
    }

    const jwt = loginResult();
    OpenAPI.TOKEN = jwt?.token;
    OpenAPI.USERNAME = jwt?.username;
    return Boolean(jwt?.token);
  });

  const logout = () => {
    setLoginRequestDTO(undefined);
    setLoginResult(() => undefined);
  };

  const value: AuthContextType = {
    login,
    isLoggedIn: loggedIn,
    register,
    jwt: loginResult,
    registerResult,
    logout,
  };

  return <AuthContext.Provider value={value}>{props.children}</AuthContext.Provider>;
};
