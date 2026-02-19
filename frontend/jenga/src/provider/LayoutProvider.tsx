import { Accessor, createContext, createSignal, JSXElement, Setter } from "solid-js";

type LayoutContextType = {
    openChat: Accessor<boolean>;
    setOpenChat: Setter<boolean>;
}

export const LayoutContext = createContext<LayoutContextType>();

interface LayoutProviderProps {
    children: JSXElement;
}

export const LayoutProvider = (props: LayoutProviderProps) => {

    const [openChat, setOpenChat] = createSignal(false);

    const value: LayoutContextType = {
        openChat,
        setOpenChat
    };

    return (
        <LayoutContext.Provider value={value}>
            {props.children}
        </LayoutContext.Provider>
    )
}