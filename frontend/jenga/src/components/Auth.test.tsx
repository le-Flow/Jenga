// @vitest-environment jsdom
import { cleanup, render, screen, waitFor } from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import { afterEach, describe, expect, test, vi } from "vitest";
import { Auth } from "./Auth";
import { createTestWrapper } from "../test/testProviders";

vi.mock("./Login", () => ({
    LogIn: () => <div data-testid="login-form" />,
}));

vi.mock("./Register", () => ({
    Register: () => <div data-testid="register-form" />,
}));

vi.mock("./UserInfo", () => ({
    UserInfo: () => <div data-testid="user-info" />,
}));

vi.mock("@solidjs/router", () => ({
    A: (props: { children?: unknown }) => <a>{props.children}</a>,
}));

afterEach(() => {
    cleanup();
    vi.restoreAllMocks();
});

describe("Auth", () => {
    test("opens auth dialog and toggles to sign-up", async () => {
        const user = userEvent.setup();

        render(() => <Auth />, {
            wrapper: createTestWrapper({ auth: { isLoggedIn: () => false } }),
        });

        await user.click(screen.getByRole("button", { name: "open-auth-dialog" }));

        await waitFor(() => {
            expect(screen.getByTestId("login-form")).toBeInTheDocument();
        });

        await user.click(screen.getByRole("button", { name: "switch-signup", hidden: true }));

        await waitFor(() => {
            expect(screen.getByTestId("register-form")).toBeInTheDocument();
        });
    });

    test("calls logout in logged-in state", async () => {
        const user = userEvent.setup();
        const logout = vi.fn();

        render(() => <Auth />, {
            wrapper: createTestWrapper({
                auth: {
                    isLoggedIn: () => true,
                    jwt: () => ({ username: "alice" }),
                    logout,
                },
            }),
        });

        await user.click(screen.getByRole("button", { name: "logout" }));
        expect(logout).toHaveBeenCalledTimes(1);
    });
});
