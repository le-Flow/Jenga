import { JSXElement, Resource, createContext, createResource, useContext } from "solid-js";
import { UserDTO, UserResourceService } from "../api";
import { AuthContext } from "./AuthProvider";

type UserContextType = {
    user: Resource<UserDTO | undefined>;
    setUser: (next: UserDTO | undefined) => void;
    refetchUser: () => Promise<UserDTO | undefined>;
};

export const UserContext = createContext<UserContextType>();

interface ProviderProps {
    children: JSXElement;
}

export const UserProvider = (props: ProviderProps) => {
    const auth = useContext(AuthContext);

    const [user, { mutate: setUser, refetch }] = createResource(
        () => (auth?.isLoggedIn() ? auth.jwt()?.username ?? null : null),
        async (username) => {
            if (!username) {
                return undefined;
            }
            return await UserResourceService.getApiUsers(username);
        }
    );

    const value: UserContextType = {
        user,
        setUser: (next) => setUser(() => next),
        refetchUser: refetch,
    };

    return <UserContext.Provider value={value}>{props.children}</UserContext.Provider>;
};
