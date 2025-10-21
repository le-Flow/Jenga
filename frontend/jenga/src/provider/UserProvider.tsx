import { createContext, Resource, createResource, JSXElement } from "solid-js"

type UserContextType = {

}

export const UserContext = createContext<UserContextType>()

interface ProviderProps {
    children: JSXElement
}

export const UserProvider = (props: ProviderProps) => {



    const value = {

    }

    return (
        <UserContext.Provider value={value}>
            {props.children}
        </UserContext.Provider>
    )
}