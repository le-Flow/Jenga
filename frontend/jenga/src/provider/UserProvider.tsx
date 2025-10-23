import { JSXElement, Resource, createContext, createResource, useContext } from "solid-js";
import { User, UserResourceService } from "../api";
import { AuthContext } from "./AuthProvider";

type UserContextType = {
    user: Resource<User | undefined>;
    setUser: (next: User | undefined) => void;
    refetchUser: () => Promise<User | undefined>;
};

export const UserContext = createContext<UserContextType>();

interface ProviderProps {
    children: JSXElement;
}

export const UserProvider = (props: ProviderProps) => {
    const auth = useContext(AuthContext);

    const [user, { mutate: setUser, refetch }] = createResource(
        () => auth?.jwt()?.username ?? null,
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
