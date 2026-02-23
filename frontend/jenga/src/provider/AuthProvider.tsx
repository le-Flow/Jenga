import { Accessor, JSXElement, createContext, createMemo, createSignal } from "solid-js";
import { AuthenticationResourceService, LoginRequestDTO, LoginResponseDTO, OpenAPI, RegisterRequestDTO } from "../api";

type AuthContextType = {
  login?: (request: LoginRequestDTO) => Promise<void>;
  isLoggedIn: Accessor<boolean>;
  register?: (request: RegisterRequestDTO) => Promise<void>;
  jwt: Accessor<LoginResponseDTO | undefined>;
  loginError: Accessor<unknown>;
  registerError: Accessor<unknown>;
  loginLoading: Accessor<boolean>;
  registerLoading: Accessor<boolean>;
  logout?: () => void;
};

export const AuthContext = createContext<AuthContextType>();

interface ProviderProps {
  children: JSXElement;
}

export const AuthProvider = (props: ProviderProps) => {
  const [jwt, setJwt] = createSignal<LoginResponseDTO>();
  const [loginError, setLoginError] = createSignal<unknown>();
  const [registerError, setRegisterError] = createSignal<unknown>();
  const [loginLoading, setLoginLoading] = createSignal(false);
  const [registerLoading, setRegisterLoading] = createSignal(false);

  const setSession = (nextJwt: LoginResponseDTO | undefined) => {
    OpenAPI.TOKEN = nextJwt?.token;
    OpenAPI.USERNAME = nextJwt?.username;
    setJwt(() => nextJwt);
  };

  const register = async (request: RegisterRequestDTO) => {
    setRegisterLoading(true);
    setRegisterError(undefined);

    try {
      const registeredUser = await AuthenticationResourceService.postApiAuthRegister(request);
      setSession(registeredUser);
      setLoginError(undefined);
    } catch (error) {
      setRegisterError(error);
    } finally {
      setRegisterLoading(false);
    }
  };

  const login = async (request: LoginRequestDTO) => {
    setLoginLoading(true);
    setLoginError(undefined);

    try {
      const loggedInUser = await AuthenticationResourceService.postApiAuthLogin(request);
      setSession(loggedInUser);
      setRegisterError(undefined);
    } catch (error) {
      setSession(undefined);
      setLoginError(error);
    } finally {
      setLoginLoading(false);
    }
  };

  const loggedIn = createMemo(() => {
    const currentJwt = jwt();
    return Boolean(currentJwt?.token);
  });

  const logout = () => {
    setSession(undefined);
    setLoginError(undefined);
    setRegisterError(undefined);
    setLoginLoading(false);
    setRegisterLoading(false);
  };

  const value: AuthContextType = {
    login,
    isLoggedIn: loggedIn,
    register,
    jwt,
    loginError,
    registerError,
    loginLoading,
    registerLoading,
    logout,
  };

  return <AuthContext.Provider value={value}>{props.children}</AuthContext.Provider>;
};
